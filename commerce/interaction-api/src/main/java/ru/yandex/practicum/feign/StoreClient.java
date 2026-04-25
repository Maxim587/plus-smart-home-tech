package ru.yandex.practicum.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface StoreClient {
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    boolean removeProductFromStore(@RequestBody UUID productId);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}

