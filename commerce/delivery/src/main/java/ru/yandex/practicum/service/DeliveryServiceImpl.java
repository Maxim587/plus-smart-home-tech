package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.enums.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private static final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5);
    private static final BigDecimal WAREHOUSE_ADDRESS_1_COEFF = BigDecimal.valueOf(1);
    private static final BigDecimal WAREHOUSE_ADDRESS_2_COEFF = BigDecimal.valueOf(2);
    private static final BigDecimal DELIVERY_ADDRESS_COEFF = BigDecimal.valueOf(0.2);
    private static final BigDecimal FRAGILE_COEFF = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_COEFF = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_COEFF = BigDecimal.valueOf(0.2);
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public BigDecimal deliveryCost(OrderDto request) {
        BigDecimal deliveryCost = BASE_DELIVERY_COST;
        Delivery delivery = findDeliveryByOrderId(request.getOrderId());
        Address warehouseAddress = delivery.getFromAddress();

        deliveryCost = switch (warehouseAddress.getStreet()) {
            case "ADDRESS_1" -> deliveryCost.add(deliveryCost.multiply(WAREHOUSE_ADDRESS_1_COEFF));
            case "ADDRESS_2" -> deliveryCost.add(deliveryCost.multiply(WAREHOUSE_ADDRESS_2_COEFF));
            default -> throw new NotFoundException("Неизвестный адрес склада " + warehouseAddress.getCountry());
        };

        if (request.getFragile()) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(FRAGILE_COEFF));
        }

        BigDecimal deliveryWeight = BigDecimal.valueOf(request.getDeliveryWeight());
        deliveryCost = deliveryCost.add(deliveryWeight.multiply(WEIGHT_COEFF));

        BigDecimal deliveryVolume = BigDecimal.valueOf(request.getDeliveryVolume());
        deliveryCost = deliveryCost.add(deliveryVolume.multiply(VOLUME_COEFF));

        if (!warehouseAddress.getStreet().equalsIgnoreCase(delivery.getToAddress().getStreet())) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(DELIVERY_ADDRESS_COEFF));
        }
        return deliveryCost;
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);
        orderClient.deliveryFailed(orderId);
        delivery.setState(DeliveryState.FAILED);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(orderId, delivery.getId());
        warehouseClient.shippedToDelivery(request);
        delivery.setState(DeliveryState.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = findDeliveryByOrderId(orderId);
        orderClient.delivery(orderId);
        delivery.setState(DeliveryState.DELIVERED);
    }

    @Override
    @Transactional
    public DeliveryDto
    planDelivery(DeliveryDto request) {
        Delivery delivery = deliveryMapper.mapDeliveryDtoToDelivery(request);
        delivery = deliveryRepository.save(delivery);
        return deliveryMapper.mapDeliveryToDeliveryDto(delivery);
    }

    private Delivery findDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Номер заказа не найден" + orderId));
    }
}
