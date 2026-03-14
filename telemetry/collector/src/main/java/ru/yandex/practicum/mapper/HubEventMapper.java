package ru.yandex.practicum.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.*;
import ru.yandex.practicum.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HubEventMapper {

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event);

    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event);



    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    DeviceAddedEventAvro mapDeviceAddedPayloadProtoToAvro(DeviceAddedEventProto payload);

    DeviceRemovedEventAvro mapDeviceRemovedPayloadProtoToAvro(DeviceRemovedEventProto payload);

    @Mapping(target = "conditions", expression = "java(mapScenarioConditionListProtoToAvro(payload.getConditionList()))")
    @Mapping(target = "actions", expression = "java(mapDeviceActionListProtoToAvro(payload.getActionList()))")
    ScenarioAddedEventAvro mapScenarioAddedPayloadProtoToAvro(ScenarioAddedEventProto payload);

    ScenarioRemovedEventAvro mapScenarioRemovedPayloadProtoToAvro(ScenarioRemovedEventProto payload);

    @Mapping(target = "payload", expression = "java(mapDeviceAddedPayloadProtoToAvro(event.getDeviceAdded()))")
    HubEventAvro mapDeviceAddedEventProtoToAvro(HubEventProto event);

    @Mapping(target = "payload", expression = "java(mapDeviceRemovedPayloadProtoToAvro(event.getDeviceRemoved()))")
    HubEventAvro mapDeviceRemovedEventProtoToAvro(HubEventProto event);

    @Mapping(target = "payload", expression = "java(mapScenarioAddedPayloadProtoToAvro(event.getScenarioAdded()))")
    HubEventAvro mapScenarioAddedEventProtoToAvro(HubEventProto event);

    @Mapping(target = "payload", expression = "java(mapScenarioRemovedPayloadProtoToAvro(event.getScenarioRemoved()))")
    HubEventAvro mapScenarioRemovedEventProtoToAvro(HubEventProto event);

    default Instant mapTimestampToInstant(Timestamp timestamp) {
        return timestamp == null ? null : Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    List<ScenarioConditionAvro> mapScenarioConditionListProtoToAvro(List<ScenarioConditionProto> conditions);

    @Mapping(target = "value", expression = "java(mapScenarioConditionValue(condition))")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    ScenarioConditionAvro mapScenarioConditionProtoToAvro(ScenarioConditionProto condition);

    default Object mapScenarioConditionValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case BOOL_VALUE -> condition.getBoolValue();
            case INT_VALUE -> condition.getIntValue();
            case VALUE_NOT_SET -> null;
        };
    }

    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = "UNRECOGNIZED")
    List<DeviceActionAvro> mapDeviceActionListProtoToAvro(List<DeviceActionProto> actions);
}
