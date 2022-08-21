package com.shoppy.controller.mapper;

import com.shoppy.controller.dto.AddressDTO;
import com.shoppy.controller.dto.CardPaymentMethodDTO;
import com.shoppy.controller.dto.ItemDTO;
import com.shoppy.controller.dto.OrderDTO;
import com.shoppy.controller.dto.ProductDTO;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Address;
import com.shoppy.model.Item;
import com.shoppy.model.Order;
import com.shoppy.model.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMapper {

    @Mapping(target = "id", ignore = true)
    Address dtoToAddress(AddressDTO dto);

    @Mapping(target = "id", ignore = true)
    Product dtoToProduct(ProductDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    Item dtoToItem(ItemDTO dto);

    @Mapping(target = "id", ignore = true)
    CardPaymentMethod dtoToCardPaymentMethod(CardPaymentMethodDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Order dtoToOrder(OrderDTO dto);

}
