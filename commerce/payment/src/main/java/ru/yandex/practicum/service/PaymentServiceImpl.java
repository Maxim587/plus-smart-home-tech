package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.enums.PaymentState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal VAT_RATE = new BigDecimal("10.0");
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreClient storeClient;
    private final OrderClient orderClient;

    @Override
    public BigDecimal getTotalCost(OrderDto request) {
        BigDecimal productPrice = request.getProductPrice();
        BigDecimal deliveryPrice = request.getDeliveryPrice();
        BigDecimal productPriceWithVat = productPrice.add(productPrice.multiply(VAT_RATE));
        return productPriceWithVat.add(deliveryPrice);
    }


    @Override
    @Transactional
    public PaymentDto payment(OrderDto request) {
        Payment payment = new Payment();
        payment.setState(PaymentState.PENDING);
        payment.setDeliveryTotal(request.getDeliveryPrice());
        payment.setTotalPayment(request.getTotalPrice());
        payment.setFeeTotal(request.getProductPrice());
        payment.setOrderId(request.getOrderId());
        payment = paymentRepository.save(payment);
        return paymentMapper.mapPaymentToPaymentDto(payment);
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж с id: " + paymentId + " не найден"));
        payment.setState(PaymentState.FAILED);
        Optional.ofNullable(orderClient.payment(payment.getOrderId()))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж с id: " + paymentId + " не найден"));
        Optional.ofNullable(orderClient.paymentSuccess(payment.getOrderId()))
                .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
        payment.setState(PaymentState.SUCCESS);
    }

    @Override
    @Transactional
    public BigDecimal productCost(OrderDto request) {
        Map<UUID, Integer> products = request.getProducts();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            ProductDto productDto = Optional.ofNullable(storeClient.getProduct(entry.getKey()))
                    .orElseThrow(() -> new InternalServerErrorException("Сервис временно недоступен"));
            BigDecimal price = productDto.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            totalCost = totalCost.add(price.multiply(quantity));
        }
        return totalCost;
    }
}
