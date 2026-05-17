package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    BigDecimal getTotalCost(OrderDto request);

    PaymentDto payment(OrderDto request);

    void paymentFailed(UUID paymentId);

    void paymentSuccess(UUID paymentId);

    BigDecimal productCost(OrderDto request);
}
