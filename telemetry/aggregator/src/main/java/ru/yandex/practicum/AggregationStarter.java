package ru.yandex.practicum;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.AggregatorKafkaProducer;
import ru.yandex.practicum.kafka.KafkaConfiguration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private static final int MIN_RECORDS_AMOUNT_TO_COMMIT_OFFSETS = 10;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final AggregatorKafkaProducer producer;
    private final SnapshotService snapshotService;
    private final KafkaConfiguration config;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        Duration consumeAttemptTimeout = Duration.ofMillis(config.getConsumeAttemptTimeoutMs());
        List<String> topics = List.of(config.getSensorEventTopic());
        try {
            log.debug("Создание подписки на топики: {}", topics);
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(consumeAttemptTimeout);
                int count = 0;
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignores) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Завершение работы консьюмера");
                consumer.close();
                log.info("Завершение работы продюсера");
                producer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, SensorEventAvro> record) {
        snapshotService.updateState(record.value())
                .ifPresent(snapshot -> producer.send(new ProducerRecord<>(config.getSnapshotTopic(), snapshot)));
    }

    private static void manageOffsets(ConsumerRecord<String, SensorEventAvro> record, int count, KafkaConsumer<String, SensorEventAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
        if (count % MIN_RECORDS_AMOUNT_TO_COMMIT_OFFSETS == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }
}

