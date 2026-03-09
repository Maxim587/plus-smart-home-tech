package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.device.*;
import ru.yandex.practicum.dto.hub.*;
import ru.yandex.practicum.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.DeviceEventMapper;
import ru.yandex.practicum.mapper.HubEventMapper;

@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final TelemetryKafkaProducer producer;
    @Value("${collector.kafka.producer.topics.sensors-events}")
    private String deviceEventTopic;
    @Value("${collector.kafka.producer.topics.hubs-events}")
    private String hubEventTopic;
    private final DeviceEventMapper deviceEventMapper;
    private final HubEventMapper hubEventMapper;

    @Override
    public void handleSensorEvent(SensorEvent event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(deviceEventTopic, getSensorEventAvroRecord(event));
        producer.send(record);
    }

    @Override
    public void handleHubEvent(HubEvent event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(hubEventTopic, getHubEventAvroRecord(event));
        producer.send(record);
    }

    private SpecificRecordBase getSensorEventAvroRecord(SensorEvent event) {
        SpecificRecordBase payload = switch (event.getType()) {
            case LIGHT_SENSOR_EVENT -> deviceEventMapper.toLightSensorAvro((LightSensorEvent) event);
            case SWITCH_SENSOR_EVENT -> deviceEventMapper.toSwitchSensorAvro((SwitchSensorEvent) event);
            case MOTION_SENSOR_EVENT -> deviceEventMapper.toMotionSensorAvro((MotionSensorEvent) event);
            case CLIMATE_SENSOR_EVENT -> deviceEventMapper.toClimateSensorAvro((ClimateSensorEvent) event);
            case TEMPERATURE_SENSOR_EVENT -> deviceEventMapper.toTemperatureSensorAvro((TemperatureSensorEvent) event);
        };

        return SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setId(event.getId())
                .setPayload(payload)
                .build();
    }

    private SpecificRecordBase getHubEventAvroRecord(HubEvent event) {
        SpecificRecordBase payload = switch (event.getEventType()) {
            case DEVICE_ADDED -> hubEventMapper.toDeviceAddedEventAvro((DeviceAddedEvent) event);
            case DEVICE_REMOVED -> hubEventMapper.toDeviceRemovedEventAvro((DeviceRemovedEvent) event);
            case SCENARIO_ADDED -> hubEventMapper.toScenarioAddedEventAvro((ScenarioAddedEvent) event);
            case SCENARIO_REMOVED -> hubEventMapper.toScenarioRemovedEventAvro((ScenarioRemovedEvent) event);
        };

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
