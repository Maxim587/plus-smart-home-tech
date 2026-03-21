package ru.yandex.practicum.repository;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotStorage {
    SensorsSnapshotAvro getSnapshotByBubId(String hubId);

    SensorsSnapshotAvro addSnapshot(SensorsSnapshotAvro snapshot);
}
