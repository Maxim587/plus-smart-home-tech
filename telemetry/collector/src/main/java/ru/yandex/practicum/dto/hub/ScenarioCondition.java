package ru.yandex.practicum.dto.hub;

import lombok.Data;

@Data
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private int value;
}
