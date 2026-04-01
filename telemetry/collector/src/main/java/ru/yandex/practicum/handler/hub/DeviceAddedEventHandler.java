package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaProducerConfiguration;
import ru.yandex.practicum.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final HubEventMapper mapper;
    private final KafkaProducerConfiguration kafkaProducerConfig;
    private final TelemetryKafkaProducer producer;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        HubEventAvro avro = mapper.mapDeviceAddedEventProtoToAvro(event);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(kafkaProducerConfig.getHubEventTopic(), avro);
        producer.send(record);
    }
}
