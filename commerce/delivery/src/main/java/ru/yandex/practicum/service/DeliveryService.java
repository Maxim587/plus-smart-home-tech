package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    BigDecimal deliveryCost(OrderDto request);

    void deliveryFailed(UUID orderId);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    DeliveryDto planDelivery(DeliveryDto request);
}
