package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.feign.PaymentClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PaymentClientFallback implements PaymentClient {
    @Override
    public BigDecimal getTotalCost(OrderDto request) {
        log.error("Fallback response: payment service is unavailable");
        return null;
    }

    @Override
    public PaymentDto payment(OrderDto request) {
        log.error("Fallback response: payment service is unavailable");
        return null;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.error("Fallback response: payment service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.error("Fallback response: payment service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public BigDecimal productCost(OrderDto request) {
        log.error("Fallback response: payment service is unavailable");
        return null;
    }
}
