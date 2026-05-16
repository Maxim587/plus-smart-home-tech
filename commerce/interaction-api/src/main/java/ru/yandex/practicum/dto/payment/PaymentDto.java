package ru.yandex.practicum.dto.payment;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PaymentDto {

    @NotNull
    private UUID paymentId;

    @Positive
    private BigDecimal totalPayment;

    @Positive
    private BigDecimal deliveryTotal;

    @Positive
    private BigDecimal feeTotal;

    private UUID orderId;
}
