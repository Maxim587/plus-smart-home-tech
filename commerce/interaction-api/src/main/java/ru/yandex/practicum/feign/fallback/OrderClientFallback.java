package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.feign.OrderClient;

import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class OrderClientFallback implements OrderClient {
    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request, String username) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto assemble(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto paymentSuccess(UUID orderId) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.error("Fallback response: order service is unavailable");
        return null;
    }
}
