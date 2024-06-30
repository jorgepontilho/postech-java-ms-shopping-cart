package com.postech.shoppingcart.mapper;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(target="id", source = "cart.id")
    @Mapping(target="items", source = "cart.items")
    CartDTO toCartDTO(Cart cart);
    Cart toCart(CartDTO cartDTO);

    @Mapping(target="id", source = "cartItem.id")
    CartItemDTO toCartItemDTO(CartItem cartItem);
    CartItem toCartItem(CartItemDTO cartItemDTO);
}