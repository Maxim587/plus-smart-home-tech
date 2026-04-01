package ru.yandex.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.handler.hub.HubEventType;
import ru.yandex.practicum.kafka.AnalyzerKafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final AnalyzerKafkaConfig config;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer,
                             AnalyzerKafkaConfig config,
                             Set<HubEventHandler> hubEventHandlers) {
        this.consumer = consumer;
        this.config = config;
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }


    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        Duration consumeAttemptTimeout = Duration.ofMillis(config.getConsumeAttemptTimeoutMs());
        List<String> topics = List.of(config.getHubEventTopic());
        try {
            log.debug("Создание подписки на топики: {}", topics);
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(consumeAttemptTimeout);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    log.info("Получено событие {} из топика: {}", record.value(), record.topic());
                    log.info("Событие отправляется в обработчик");
                    handleRecord(record);
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignores) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хаба", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Завершение работы консьюмера");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro event = record.value();
        Object payload = event.getPayload();

        HubEventHandler handler = switch (payload) {
            case DeviceAddedEventAvro ignored -> hubEventHandlers.get(HubEventType.DEVICE_ADDED);
            case DeviceRemovedEventAvro ignored -> hubEventHandlers.get(HubEventType.DEVICE_REMOVED);
            case ScenarioAddedEventAvro ignored -> hubEventHandlers.get(HubEventType.SCENARIO_ADDED);
            case ScenarioRemovedEventAvro ignored -> hubEventHandlers.get(HubEventType.SCENARIO_REMOVED);
            default -> throw new RuntimeException("Событие хаба не существует: " + payload.getClass().getSimpleName());
        };
        log.info("Событие направлено в обработчик: {}", handler);
        handler.handle(event);
    }
}
