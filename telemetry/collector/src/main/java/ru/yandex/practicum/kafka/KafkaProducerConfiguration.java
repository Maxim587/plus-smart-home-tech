package ru.yandex.practicum.kafka;

import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Getter
@Configuration
public class KafkaProducerConfiguration {
    @Value("${collector.kafka.producer.properties.bootstrap.servers}")
    private List<String> bootstrapServers;
    @Value("${collector.kafka.producer.properties.key.serializer}")
    private String keySerializer;
    @Value("${collector.kafka.producer.properties.value.serializer}")
    private String valueSerializer;
    @Value("${collector.kafka.producer.properties.client.id}")
    private String clientId;
    @Value("${collector.kafka.producer.topics.sensors-events}")
    private String sensorEventTopic;
    @Value("${collector.kafka.producer.topics.hubs-events}")
    private String hubEventTopic;


    @Bean
    public Producer<String, SpecificRecordBase> producer() {
        return new KafkaProducer<>(getConfig());
    }

    @Bean
    public TelemetryKafkaProducer telemetryKafkaProducer(Producer<String, SpecificRecordBase> producer) {
        return new TelemetryKafkaProducer(producer);
    }

    private Properties getConfig() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        return config;
    }
}
