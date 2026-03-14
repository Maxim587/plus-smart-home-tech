package ru.yandex.practicum.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.KafkaProducerConfiguration;
import ru.yandex.practicum.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.SensorEventMapper;

@Component
@RequiredArgsConstructor
public class LigtSensorEventHandler implements SensorEventHandler {
    private final SensorEventMapper mapper;
    private final KafkaProducerConfiguration kafkaProducerConfig;
    private final TelemetryKafkaProducer producer;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro avro = mapper.mapLightSensorEventProtoToAvro(event);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(kafkaProducerConfig.getSensorEventTopic(), avro);
        producer.send(record);
    }
}
