package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository repository;


    public void save(Scenario scenario) {
        boolean scenarioExists = repository.existsByHubIdAndName(scenario.getHubId(), scenario.getName());

        if (scenarioExists) {
            log.info("Сценарий с названием '{}' уже существует в хабе с id {}", scenario.getName(), scenario.getHubId());
        } else {
            log.info("Выполняется сохранение сценария '{}' в БД", scenario.getName());
            repository.save(scenario);
        }
    }

    public void delete(String hubId, String name) {
        Optional<Scenario> scenarioOptional = repository.findByHubIdAndName(hubId, name);
        scenarioOptional.ifPresent(repository::delete);
    }
}
