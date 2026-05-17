package ru.yandex.practicum.dto.delivery;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.enums.DeliveryState;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class DeliveryDto {

    @NotNull
    private UUID deliveryId;

    @NotNull
    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;
}
