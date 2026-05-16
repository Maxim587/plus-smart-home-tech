package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_from_id", referencedColumnName = "id")
    private Address fromAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_to_id", referencedColumnName = "id")
    private Address toAddress;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private DeliveryState state;
}
