package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.enums.DeliveryState;
import ru.yandex.practicum.dto.enums.OrderState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NotAuthorizedUserException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.feign.CartClient;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.ProductInOrder;
import ru.yandex.practicum.repository.OrderProductRepository;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderMapper orderMapper;
    private final CartClient cartClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        checkUser(username);
        List<ProductInOrder> products = orderProductRepository.findAllByOrder_Username(username);
        return orderMapper.mapOrdersToOrderDtos(products);
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request, String username) {
        checkUser(username);
        ShoppingCartDto shoppingCartDto = request.getShoppingCart();
        checkShoppingCart(shoppingCartDto, username);
        Order order = makeOrder(shoppingCartDto, username);
        order = orderRepository.save(order);
        List<ProductInOrder> products = orderMapper.mapProductsMapToProductsInOrderList(shoppingCartDto.getProducts(), order);
        orderProductRepository.saveAll(products);
        DeliveryDto plannedDelivery = planDelivery(request.getDeliveryAddress(), order.getId());
        order.setDeliveryId(plannedDelivery.getDeliveryId());
        BigDecimal productPrice = getProductPrice(order, shoppingCartDto.getProducts());
        order.setProductPrice(productPrice);
        return orderMapper.mapOrderToOrderDto(order, shoppingCartDto.getProducts());
    }

    @Override
    @Transactional
    public OrderDto assemble(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(products)
                .build();
        Optional.ofNullable(warehouseClient.assemblyProductsForOrder(request))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        OrderDto orderDto = orderMapper.mapOrderToOrderDto(order, products);
        BigDecimal deliveryCost = Optional.ofNullable(deliveryClient.deliveryCost(orderDto))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        order.setDeliveryPrice(deliveryCost);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        OrderDto orderDto = orderMapper.mapOrderToOrderDto(order, products);
        BigDecimal totalCost = paymentClient.getTotalCost(orderDto);
        order.setTotalPrice(totalCost);
        orderDto.setTotalPrice(totalCost);
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        order.setState(OrderState.COMPLETED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        deliveryClient.deliveryPicked(orderId);
        order.setState(OrderState.ON_DELIVERY);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        OrderDto orderDto = orderMapper.mapOrderToOrderDto(order, products);
        PaymentDto paymentDto = Optional.ofNullable(paymentClient.payment(orderDto))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        order.setState(OrderState.PAID);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        Map<UUID, Integer> products = getProductsInOrder(order);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = findOrderById(request.getOrderId());
        Map<UUID, Integer> products = getProductsInOrder(order);
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.mapOrderToOrderDto(order, products);
    }

    private void checkUser(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не заполнено");
        }
    }

    private void checkShoppingCart(ShoppingCartDto shoppingCartDto, String username) {
        ShoppingCartDto shoppingCart = Optional.ofNullable(cartClient.getShoppingCart(username))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));

        if (!shoppingCart.getShoppingCartId().equals(shoppingCartDto.getShoppingCartId())) {
            throw new NotFoundException("Корзина с id " + shoppingCart.getShoppingCartId() + " не найдена");
        }
    }

    private DeliveryDto planDelivery(AddressDto deliveryAddress, UUID orderId) {
        AddressDto warehouseAddress = Optional.ofNullable(warehouseClient.getWarehouseAddress())
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .fromAddress(warehouseAddress)
                .toAddress(deliveryAddress)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();
        return Optional.ofNullable(deliveryClient.planDelivery(deliveryDto))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден" + orderId));
    }

    private Map<UUID, Integer> getProductsInOrder(Order order) {
        List<ProductInOrder> productInOrderList = orderProductRepository.findAllByOrder(order);
        return productInOrderList.stream()
                .collect(Collectors.toMap(ProductInOrder::getProductId, ProductInOrder::getQuantity));
    }

    private Order makeOrder(ShoppingCartDto shoppingCartDto, String username) {
        BookedProductsDto bookedProductsDto = Optional.ofNullable(warehouseClient.checkProductQuantityEnoughForShoppingCart(shoppingCartDto))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        return Order.builder()
                .shoppingCartId(shoppingCartDto.getShoppingCartId())
                .username(username)
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.isFragile())
                .build();
    }

    private BigDecimal getProductPrice (Order order, Map<UUID, Integer> products) {
        OrderDto orderDto = orderMapper.mapOrderToOrderDto(order, products);
        return Optional.ofNullable(paymentClient.productCost(orderDto))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
    }
}
