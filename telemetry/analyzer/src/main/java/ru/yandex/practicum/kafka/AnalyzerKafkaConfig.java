package ru.yandex.practicum.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Properties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("kafka")
public class AnalyzerKafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private List<String> bootstrapServers;
    @Value("${kafka.client.id}")
    private String clientId;
    @Value("${kafka.consumer.properties.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.properties.enable-auto-commit}")
    private String enableAutoCommit;


    @Value("${kafka.consumer.hub-event-consumer.key.deserializer}")
    private String hubEventKeyDeserializer;
    @Value("${kafka.consumer.hub-event-consumer.value.deserializer}")
    private String hubEventValueDeserializer;
    @Value("${kafka.consumer.snapshot-consumer.key.deserializer}")
    private String snapshotKeyDeserializer;
    @Value("${kafka.consumer.snapshot-consumer.value.deserializer}")
    private String snapshotValueDeserializer;

    @Value("${kafka.consumer.properties.group.hub}")
    private String hubGroupId;
    @Value("${kafka.consumer.properties.group.snapshot}")
    private String snapshotGroupId;
    @Value("${kafka.consumer.properties.consume.attempt.timeout.ms}")
    private Long consumeAttemptTimeoutMs;

    @Value("${kafka.topics.hubs-events}")
    private String hubEventTopic;
    @Value("${kafka.topics.snapshots}")
    private String snapshotTopic;


    @Bean
    public KafkaConsumer<String, HubEventAvro> hubEventKafkaConsumer() {
        Properties properties = getHubEventKafkaConsumerConfig();
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotKafkaConsumer() {
        Properties properties = getSnapshotKafkaConsumerConfig();
        return new KafkaConsumer<>(properties);
    }

    private Properties getBaseConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        return properties;
    }

    private Properties getHubEventKafkaConsumerConfig() {
        Properties properties = getBaseConfig();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubEventKeyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        return properties;
    }

    private Properties getSnapshotKafkaConsumerConfig() {
        Properties properties = getBaseConfig();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotKeyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        return properties;
    }
}
