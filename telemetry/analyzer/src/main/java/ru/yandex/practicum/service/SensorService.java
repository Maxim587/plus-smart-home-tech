package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository repository;

    public void save(Sensor sensor) {
        boolean sensorExists = repository.existsByIdAndHubId(sensor.getId(), sensor.getHubId());

        if (sensorExists) {
            log.warn("Устройство с id {} уже существует в хабе с id {}", sensor.getId(), sensor.getHubId());
        } else {
            log.info("Выполняется сохранение датчика '{}' в БД", sensor);
            repository.save(sensor);
        }
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
