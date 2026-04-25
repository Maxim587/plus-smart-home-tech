package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.QuantityState;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController implements StoreClient {
    private final ProductService productService;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable page) {
        return productService.getProducts(category, page);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @Override
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        return productService.setProductQuantityState(productId, quantityState);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return productService.removeProductFromStore(productId);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return productService.getProductById(productId);
    }
}
