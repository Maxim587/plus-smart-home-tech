package ru.yandex.practicum.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.feign.fallback.DeliveryClientFallback;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery", fallback = DeliveryClientFallback.class)
public interface DeliveryClient {

    @PostMapping("/cost")
    @ResponseStatus(HttpStatus.OK)
    BigDecimal deliveryCost(@Valid @RequestBody OrderDto request);

    @PostMapping("/failed")
    @ResponseStatus(HttpStatus.OK)
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/picked")
    @ResponseStatus(HttpStatus.OK)
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping("/successful")
    @ResponseStatus(HttpStatus.OK)
    void deliverySuccessful(@RequestBody UUID orderId);

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    DeliveryDto planDelivery(@RequestBody DeliveryDto request);
}

