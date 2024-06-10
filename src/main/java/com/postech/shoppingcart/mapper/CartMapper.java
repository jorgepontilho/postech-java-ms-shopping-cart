package com.postech.shoppingcart.mapper;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDTO(Cart cart);
    Cart toEntity(CartDTO cartDTO);
}