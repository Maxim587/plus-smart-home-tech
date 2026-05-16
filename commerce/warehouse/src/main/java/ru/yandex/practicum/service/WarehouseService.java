package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProductInWarehouse(NewProductInWarehouseRequest dto);

    void addProductQuantityToWarehouse(AddProductToWarehouseRequest dto);

    AddressDto getWarehouseAddress();

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto dto);

    void acceptReturn(Map<UUID, Integer> products);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);
}
