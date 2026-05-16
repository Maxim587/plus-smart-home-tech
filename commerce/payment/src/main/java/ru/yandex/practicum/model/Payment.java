package ru.yandex.practicum.model;


import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    private UUID orderId;
}
