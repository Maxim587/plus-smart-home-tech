package ru.yandex.practicum.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemorySnapshotStorageImpl implements SnapshotStorage {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public SensorsSnapshotAvro getSnapshotByBubId(String hubId) {
        return snapshots.get(hubId);
    }

    @Override
    public SensorsSnapshotAvro addSnapshot(SensorsSnapshotAvro snapshot) {
        snapshots.put(snapshot.getHubId(), snapshot);
        return snapshot;
    }
}
