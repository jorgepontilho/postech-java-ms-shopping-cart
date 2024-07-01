package com.postech.shoppingcart;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Cart createTestCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setTotal(BigDecimal.ZERO);
        return cart;
    }

    public static CartDTO createTestCartDTO() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(1L);
        cartDTO.setTotal(BigDecimal.ZERO);
        return cartDTO;
    }

    public static CartItem createTestCartItem(Long id, Long productId, int quantity, BigDecimal price, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(price);
        cartItem.setCart(cart);
        return cartItem;
    }

    public static CartItemDTO createTestCartItemDTO(Long productId, int quantity, BigDecimal price) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(productId);
        cartItemDTO.setQuantity(quantity);
        cartItemDTO.setPrice(price);
        return cartItemDTO;
    }

    public static List<CartItem> createTestCartItems(Cart cart) {
        List<CartItem> items = new ArrayList<>();
        items.add(createTestCartItem(101L, 1L, 2, BigDecimal.valueOf(10.5), cart));
        items.add(createTestCartItem(102L, 2L, 1, BigDecimal.valueOf(15.0), cart));
        return items;
    }

    public static List<CartItemDTO> createTestCartItemDTOs() {
        List<CartItemDTO> items = new ArrayList<>();
        items.add(createTestCartItemDTO(1L, 2, BigDecimal.valueOf(10.5)));
        items.add(createTestCartItemDTO(2L, 1, BigDecimal.valueOf(15.0)));
        return items;
    }
}

