package com.postech.shoppingcart.service;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.BadRequestException;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.mapper.CartMapper;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import com.postech.shoppingcart.repository.CartItemRepository;
import com.postech.shoppingcart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public CartItem createOrUpdateCartItem(Cart cart, CartItemDTO cartItemDTO) {
        try {
            Optional<CartItem> existingCartItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(cartItemDTO.getProductId()))
                    .findFirst();

            CartItem item;
            if (existingCartItem.isPresent()) {
                // If the item already exists in the cart, update its quantity
                item = existingCartItem.get();
                item.setQuantity(item.getQuantity() + cartItemDTO.getQuantity());
            } else {
                // If the item is new, create a new CartItem and add it to the cart
                item = new CartItem();
                item.setProductId(cartItemDTO.getProductId());
                item.setQuantity(cartItemDTO.getQuantity());
                // ... set other properties (price, name, etc.) based on productClient.getProductById()
                item.setCart(cart);
            }
            return cartItemRepository.save(item);
        } catch (Exception e) {
            log.error("Unexpected error adding item to cart: {}", e.getMessage());
            throw e;
        }
    }
}