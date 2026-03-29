package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.SensorService;


@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final SensorService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventAvro event) {
        String sensorId = ((DeviceRemovedEventAvro) event.getPayload()).getId();
        service.delete(sensorId);
    }
}
