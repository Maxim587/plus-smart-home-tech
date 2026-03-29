package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioRepository repository;
    @GrpcClient("hub-router")
    HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public void handleSnapshot(ConsumerRecord<String, SensorsSnapshotAvro> snapshotRecord) {
        SensorsSnapshotAvro snapshot = snapshotRecord.value();
        List<Scenario> scenarios = repository.findAllByHubId(snapshot.getHubId());
        Map<String, SensorStateAvro> states = snapshot.getSensorsState();

        scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), states))
                .flatMap(scenario ->  getDeviceActionRequestList(scenario).stream())
                .forEach(request -> client.handleDeviceAction(request));
    }

    private List<DeviceActionRequest> getDeviceActionRequestList(Scenario scenario) {
        Instant ts = Instant.now();
        Map<String, Action> actions = scenario.getActions();
        return actions.entrySet().stream().map(entry ->
                        DeviceActionRequest.newBuilder()
                                .setHubId(scenario.getHubId())
                                .setScenarioName(scenario.getName())
                                .setAction(DeviceActionProto.newBuilder()
                                        .setSensorId(entry.getKey())
                                        .setType(ActionTypeProto.valueOf(entry.getValue().getType().name()))
                                        .setValue(Objects.isNull(entry.getValue().getValue()) ? 0 : entry.getValue().getValue())
                                        .build())
                                .setTimestamp(Timestamp.newBuilder()
                                        .setSeconds(ts.getEpochSecond())
                                        .setNanos(ts.getNano())
                                        .build())
                                .build())
                .toList();
    }

    private boolean checkConditions(Map<String, Condition> conditions, Map<String, SensorStateAvro> states) {
        return conditions.entrySet().stream().allMatch(conditionEntry -> {
                    String sensorId = conditionEntry.getKey();
                    SensorStateAvro state = states.get(sensorId);
                    if (Objects.isNull(state)) {
                        return false;
                    }
                    Condition condition = conditionEntry.getValue();
                    Optional<Integer> valueOpt = getSensorValue(condition, state);
                    return valueOpt.filter(integer -> compareValues(integer, condition.getValue(), condition.getOperation())).isPresent();
                }
        );
    }

    private Optional<Integer> getSensorValue(Condition condition, SensorStateAvro state) {
        ConditionType type = condition.getType();
        Object data = state.getData();
        log.info("Определение значения датчика для типа условия {}", type);
        Integer value = switch (data) {
            case ClimateSensorAvro val -> switch (type) {
                case TEMPERATURE -> val.getTemperatureC();
                case HUMIDITY -> val.getHumidity();
                case CO2LEVEL -> val.getCo2Level();
                default -> null;
            };
            case LightSensorAvro val -> switch (type) {
                case LUMINOSITY -> val.getLuminosity();
                default -> null;
            };
            case MotionSensorAvro val -> switch (type) {
                case MOTION -> val.getMotion() ? 1 : 0;
                default -> null;
            };
            case SwitchSensorAvro val -> switch (type) {
                case SWITCH -> val.getState() ? 1 : 0;
                default -> null;
            };
            case TemperatureSensorAvro val -> switch (type) {
                case TEMPERATURE -> val.getTemperatureC();
                default -> null;
            };
            default -> {
                log.warn("Сенсор не найден: {}", data);
                yield null;
            }
        };
        log.info("Определено значение датчика: {}", value);
        return Optional.ofNullable(value);
    }

    private boolean compareValues(Integer currentValue, Integer conditionValue, ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> currentValue.equals(conditionValue);
            case GREATER_THAN -> currentValue > conditionValue;
            case LOWER_THAN -> currentValue < conditionValue;
        };
    }
}
