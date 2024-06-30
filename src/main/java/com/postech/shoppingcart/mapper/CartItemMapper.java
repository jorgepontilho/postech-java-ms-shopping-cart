package com.postech.shoppingcart.mapper;

import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(target="id", source = "cartItem.id")
    CartItemDTO toCartItemDTO(CartItem cartItem);
    CartItem toCartItem(CartItemDTO cartItemDTO);
}