package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.ProductInOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    @Mapping(target = "orderId", source = "order.id")
    OrderDto mapOrderToOrderDto(Order order, Map<UUID, Integer> products);

    default List<OrderDto> mapOrdersToOrderDtos(List<ProductInOrder> products) {
        Map<Order, Map<UUID, Integer>> orderProducts = new HashMap<>();

        for (ProductInOrder productInOrder : products) {
            orderProducts.computeIfAbsent(productInOrder.getOrder(), k -> new HashMap<>())
                    .put(productInOrder.getProductId(), productInOrder.getQuantity());
        }

        return orderProducts.entrySet().stream()
                .map(entry -> mapOrderToOrderDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    default List<ProductInOrder> mapProductsMapToProductsInOrderList(Map<UUID, Integer> products, Order order) {
        return products.entrySet().stream()
                .map(entry ->
                        ProductInOrder.builder()
                                .productId(entry.getKey())
                                .quantity(entry.getValue())
                                .order(order)
                                .build())
                .toList();
    }
}