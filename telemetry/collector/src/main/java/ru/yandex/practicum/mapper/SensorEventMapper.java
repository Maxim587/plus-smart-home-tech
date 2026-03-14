package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SensorEventMapper {

    LightSensorAvro mapLightSensorPayloadProtoToAvro(LightSensorProto payload);

    SwitchSensorAvro mapSwitchSensorPayloadProtoToAvro(SwitchSensorProto payload);

    MotionSensorAvro mapMotionSensorPayloadProtoToAvro(MotionSensorProto payload);

    ClimateSensorAvro mapClimateSensorPayloadProtoToAvro(ClimateSensorProto payload);

    TemperatureSensorAvro mapTemperatureSensorPayloadProtoToAvro(TemperatureSensorProto payload);


    @Mapping(target = "payload", expression = "java(mapLightSensorPayloadProtoToAvro(event.getLightSensor()))")
    SensorEventAvro mapLightSensorEventProtoToAvro(SensorEventProto event);

    @Mapping(target = "payload", expression = "java(mapSwitchSensorPayloadProtoToAvro(event.getSwitchSensor()))")
    SensorEventAvro mapSwitchSensorEventProtoToAvro(SensorEventProto event);

    @Mapping(target = "payload", expression = "java(mapMotionSensorPayloadProtoToAvro(event.getMotionSensor()))")
    SensorEventAvro mapMotionSensorEventProtoToAvro(SensorEventProto event);

    @Mapping(target = "payload", expression = "java(mapClimateSensorPayloadProtoToAvro(event.getClimateSensor()))")
    SensorEventAvro mapClimateSensorEventProtoToAvro(SensorEventProto event);

    @Mapping(target = "payload", expression = "java(mapTemperatureSensorPayloadProtoToAvro(event.getTemperatureSensor()))")
    SensorEventAvro mapTemperatureSensorEventProtoToAvro(SensorEventProto event);

    default Instant mapTimestampToInstant(Timestamp timestamp) {
        return timestamp == null ? null : Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
