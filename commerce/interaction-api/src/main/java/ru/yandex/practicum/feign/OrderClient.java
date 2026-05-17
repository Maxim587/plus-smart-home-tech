package ru.yandex.practicum.feign;


import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.feign.fallback.OrderClientFallback;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order", fallback = OrderClientFallback.class)
public interface OrderClient {

    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username);

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request, @RequestParam String username);

    @PostMapping("/assembly")
    @ResponseStatus(HttpStatus.OK)
    OrderDto assemble(@NotNull @RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    @ResponseStatus(HttpStatus.OK)
    OrderDto assemblyFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    @ResponseStatus(HttpStatus.OK)
    OrderDto calculateDeliveryCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    @ResponseStatus(HttpStatus.OK)
    OrderDto calculateTotalCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/completed")
    @ResponseStatus(HttpStatus.OK)
    OrderDto complete(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery")
    @ResponseStatus(HttpStatus.OK)
    OrderDto delivery(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    @ResponseStatus(HttpStatus.OK)
    OrderDto deliveryFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment")
    @ResponseStatus(HttpStatus.OK)
    OrderDto payment(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    @ResponseStatus(HttpStatus.OK)
    OrderDto paymentFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment/success")
    @ResponseStatus(HttpStatus.OK)
    OrderDto paymentSuccess(UUID orderId);

    @PostMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    OrderDto productReturn(@RequestBody ProductReturnRequest request);
}

