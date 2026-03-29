package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.Sensor;

import java.util.Map;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HubEventMapper {

    @Mapping(target = "id", source = "event.id")
    Sensor mapDeviceAddedEventAvroToSensor(DeviceAddedEventAvro event, String hubId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "event.name")
    @Mapping(target = "conditions", source = "conditions")
    @Mapping(target = "actions", source = "actions")
    Scenario mapScenarioAddedEventAvroToScenario(ScenarioAddedEventAvro event,
                                                 String hubId,
                                                 Map<String, Condition> conditions,
                                                 Map<String, Action> actions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "value", expression = "java(mapConditionValueToInteger(conditionAvro))")
    Condition mapScenarioConditionAvroToCondition(ScenarioConditionAvro conditionAvro);

    default Integer mapConditionValueToInteger(ScenarioConditionAvro conditionAvro) {
        return switch (conditionAvro.getValue()) {
            case null -> null;
            case Boolean val -> val ? 1 : 0;
            case Integer val -> val;
            default ->
                    throw new IllegalArgumentException("Некорректное значение поля value: " + conditionAvro.getValue());
        };
    }

    Map<String, Condition> mapConditionAvroMapToConditionMap(Map<String, ScenarioConditionAvro> conditionAvroMap);

    @Mapping(target = "id", ignore = true)
    Action mapDeviceActionAvroToAction(DeviceActionAvro actionAvro);

    Map<String, Action> mapActionAvroMapToActionMap(Map<String, DeviceActionAvro> actionAvroMap);
}
