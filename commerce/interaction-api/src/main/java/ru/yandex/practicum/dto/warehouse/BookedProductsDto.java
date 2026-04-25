package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedProductsDto {
    @NotNull
    @Positive
    private double deliveryWeight;

    @NotNull
    @Positive
    private double deliveryVolume;

    @NotNull
    private boolean fragile;
}
