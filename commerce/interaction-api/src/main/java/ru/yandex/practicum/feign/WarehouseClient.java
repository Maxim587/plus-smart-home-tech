package ru.yandex.practicum.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
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
}

