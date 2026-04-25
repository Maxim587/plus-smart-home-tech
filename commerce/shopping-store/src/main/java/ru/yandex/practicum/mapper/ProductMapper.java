package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    Product mapProductDtoToProduct(ProductDto productDto);

    ProductDto mapProductToProductDto(Product product);

    default Page<ProductDto> mapProductsPageToProductDtoPage(Page<Product> products) {
        return products.map(this::mapProductToProductDto);
    }

    @Mapping(target = "productId", ignore = true)
    void updateProduct(ProductDto dto, @MappingTarget Product product);
}
