package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.ProductState;
import ru.yandex.practicum.dto.enums.QuantityState;
import ru.yandex.practicum.exceptions.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable page) {
        Page<Product> productsPage = productRepository.findAllByProductCategory(category, page);
        return productMapper.mapProductsPageToProductDtoPage(productsPage);
    }

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        Product product = productMapper.mapProductDtoToProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.mapProductToProductDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = findProduct(productDto.getProductId());
        productMapper.updateProduct(productDto, product);
        return productMapper.mapProductToProductDto(product);
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        Product product = findProduct(productId);
        product.setQuantityState(quantityState);
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = findProduct(productId);
        return productMapper.mapProductToProductDto(product);
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = findProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с id " + productId + " не найден"));
    }
}


