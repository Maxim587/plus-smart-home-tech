package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getClientOrders(String username);

    OrderDto createNewOrder(CreateNewOrderRequest request, String username);

    OrderDto assemble(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto complete(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto paymentSuccess(UUID orderId);

    OrderDto productReturn(ProductReturnRequest request);
}
