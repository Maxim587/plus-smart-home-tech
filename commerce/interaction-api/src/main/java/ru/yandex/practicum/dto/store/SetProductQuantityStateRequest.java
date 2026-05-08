package ru.yandex.practicum.dto.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;
import ru.yandex.practicum.dto.enums.QuantityState;

@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {

    private UUID productId;

    private QuantityState quantityState;
}
