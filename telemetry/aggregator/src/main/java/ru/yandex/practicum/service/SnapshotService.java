package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.repository.SnapshotStorage;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final SnapshotStorage snapshotStorage;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshotStorage.getSnapshotByBubId(event.getHubId());
        if (snapshot == null) {
            snapshot = createSnapshot(event);
            snapshotStorage.addSnapshot(snapshot);
        }

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = createState(event);
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {
        return SensorsSnapshotAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setHubId(event.getHubId())
                .setSensorsState(new HashMap<>())
                .build();
    }

    private SensorStateAvro createState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
