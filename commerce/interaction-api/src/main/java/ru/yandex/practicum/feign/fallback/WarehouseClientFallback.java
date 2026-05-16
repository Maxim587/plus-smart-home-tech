package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.feign.WarehouseClient;

import java.util.Map;
import java.util.UUID;


@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest dto) {
        log.error("Fallback response: warehouse service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public void addProductQuantityToWarehouse(AddProductToWarehouseRequest dto) {
        log.error("Fallback response: warehouse service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.error("Fallback response: warehouse service is unavailable");
        return null;
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto dto) {
        log.error("Fallback response: warehouse service is unavailable");
        return null;
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> products) {
        log.error("Fallback response: warehouse service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.error("Fallback response: warehouse service is unavailable");
        return null;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.error("Fallback response: warehouse service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }
}
