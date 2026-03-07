package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.device.SensorEvent;
import ru.yandex.practicum.dto.hub.HubEvent;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class CollectorController {
    private final CollectorService collectorService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        collectorService.handleSensorEvent(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        collectorService.handleHubEvent(event);
    }
}
