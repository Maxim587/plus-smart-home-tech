package ru.yandex.practicum.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.feign.fallback.WarehouseClientFallback;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    void addNewProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest dto);


    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    void addProductQuantityToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest dto);


    @GetMapping("/address")
    AddressDto getWarehouseAddress();


    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto dto);


    @PostMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    void acceptReturn(@RequestBody Map<UUID, Integer> products);


    @PostMapping("/assembly")
    @ResponseStatus(HttpStatus.OK)
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);


    @PostMapping("/shipped")
    @ResponseStatus(HttpStatus.OK)
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);
}

