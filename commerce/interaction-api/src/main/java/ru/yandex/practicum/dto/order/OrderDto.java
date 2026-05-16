package ru.yandex.practicum.dto.order;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.dto.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OrderDto {

    @NotNull
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    private Map<UUID, Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    private OrderState state;

    @Positive
    private Double deliveryWeight;

    @Positive
    private Double deliveryVolume;

    private Boolean fragile;

    @Positive
    private BigDecimal totalPrice;

    @Positive
    private BigDecimal deliveryPrice;

    @Positive
    private BigDecimal productPrice;
}
