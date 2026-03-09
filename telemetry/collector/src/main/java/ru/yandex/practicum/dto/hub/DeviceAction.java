package ru.yandex.practicum.dto.hub;

import lombok.Data;

@Data
public class DeviceAction {
    private String sensorId;
    private DeviceActionType type;
    private Integer value;

}
