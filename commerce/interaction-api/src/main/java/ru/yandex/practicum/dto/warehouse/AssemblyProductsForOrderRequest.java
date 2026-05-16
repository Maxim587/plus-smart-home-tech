package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AssemblyProductsForOrderRequest {
    @NotNull
    private UUID orderId;

    @NotNull
    private Map<UUID, Integer> products;
}