package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.device.SensorEvent;
import ru.yandex.practicum.dto.hub.HubEvent;

public interface CollectorService {
    void handleSensorEvent(SensorEvent event);

    void handleHubEvent(HubEvent event);
}
