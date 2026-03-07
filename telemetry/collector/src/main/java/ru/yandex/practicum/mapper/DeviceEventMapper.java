package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.device.*;
import ru.yandex.practicum.kafka.telemetry.event.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeviceEventMapper {

    LightSensorAvro toLightSensorAvro(LightSensorEvent event);

    SwitchSensorAvro toSwitchSensorAvro(SwitchSensorEvent event);

    MotionSensorAvro toMotionSensorAvro(MotionSensorEvent event);

    ClimateSensorAvro toClimateSensorAvro(ClimateSensorEvent event);

    TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorEvent event);
}
