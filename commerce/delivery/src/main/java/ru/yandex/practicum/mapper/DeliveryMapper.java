package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeliveryMapper {

    @Mapping(target = "deliveryId", source = "id")
    @Mapping(target = "deliveryState", source = "state")
    DeliveryDto mapDeliveryToDeliveryDto(Delivery delivery);

    AddressDto mapAddressToAddressDto(Address address);

    @Mapping(target = "id", source = "deliveryId")
    @Mapping(target = "state", source = "deliveryState")
    Delivery mapDeliveryDtoToDelivery(DeliveryDto deliveryDto);

    @Mapping(target = "id", ignore = true)
    Address mapAddressDtoToAddress(AddressDto addressDto);
}