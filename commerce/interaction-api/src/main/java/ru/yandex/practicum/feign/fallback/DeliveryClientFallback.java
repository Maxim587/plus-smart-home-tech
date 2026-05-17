package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.feign.DeliveryClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class DeliveryClientFallback implements DeliveryClient {
    @Override
    public BigDecimal deliveryCost(OrderDto request) {
        log.error("Fallback response: delivery service is unavailable");
        return null;
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        log.error("Fallback response: delivery service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        log.error("Fallback response: delivery service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        log.error("Fallback response: delivery service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public DeliveryDto planDelivery(DeliveryDto request) {
        log.error("Fallback response: delivery service is unavailable");
        return null;
    }
}
