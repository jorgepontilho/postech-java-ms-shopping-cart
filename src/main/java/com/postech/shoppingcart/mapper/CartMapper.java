package com.postech.shoppingcart.mapper;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);


    CartDTO toDTO(Cart cart);
    Cart toEntity(CartDTO cartDTO);
}