package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.service.ScenarioService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final HubEventMapper mapper;
    private final ScenarioService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAvro = (ScenarioAddedEventAvro) event.getPayload();
        Map<String, ScenarioConditionAvro> conditionAvroMap = scenarioAvro.getConditions().stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, Function.identity()));
        Map<String, DeviceActionAvro> actionAvroMap = scenarioAvro.getActions().stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, Function.identity()));
        Map<String, Condition> conditionMap = mapper.mapConditionAvroMapToConditionMap(conditionAvroMap);
        Map<String, Action> actionMap = mapper.mapActionAvroMapToActionMap(actionAvroMap);
        Scenario scenario = mapper.mapScenarioAddedEventAvroToScenario(scenarioAvro, event.getHubId(), conditionMap, actionMap);
        service.save(scenario);
    }

}
