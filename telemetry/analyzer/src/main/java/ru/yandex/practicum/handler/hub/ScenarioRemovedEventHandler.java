package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.ScenarioService;


@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioAvro = (ScenarioRemovedEventAvro) event.getPayload();
        service.delete(event.getHubId(), scenarioAvro.getName());
    }
}
