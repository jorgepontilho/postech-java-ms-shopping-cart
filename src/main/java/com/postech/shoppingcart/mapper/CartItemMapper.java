package com.postech.shoppingcart.mapper;

import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemDTO toDTO(CartItem cartItem);
    CartItem toEntity(CartItemDTO cartItemDTO);
}