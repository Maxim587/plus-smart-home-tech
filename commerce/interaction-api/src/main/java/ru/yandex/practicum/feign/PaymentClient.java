package ru.yandex.practicum.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.feign.fallback.PaymentClientFallback;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment", fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/totalCost")
    @ResponseStatus(HttpStatus.OK)
    BigDecimal getTotalCost(@RequestBody OrderDto request);

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    PaymentDto payment(@RequestBody OrderDto request);

    @PostMapping("/failed")
    @ResponseStatus(HttpStatus.OK)
    void paymentFailed(@RequestBody UUID paymentId);

    @PostMapping("/refund")
    @ResponseStatus(HttpStatus.OK)
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    @ResponseStatus(HttpStatus.OK)
    BigDecimal productCost(@RequestBody OrderDto request);
}

