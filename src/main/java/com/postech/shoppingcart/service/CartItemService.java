package com.postech.shoppingcart.service;

import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import com.postech.shoppingcart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

            CartItem item = getNewCartItem(cart, cartItemDTO, existingCartItem);
            return cartItemRepository.save(item);
        } catch (Exception e) {
            log.error("Unexpected error adding item to cart: {}", e.getMessage());
            throw e;
        }
    }

    private static CartItem getNewCartItem(Cart cart, CartItemDTO cartItemDTO, Optional<CartItem> existingCartItem) {
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
            item.setPrice(cartItemDTO.getPrice());
            item.setCart(cart);
        }
        return item;
    }

    public void removeItem(Long itemId, Cart cart) {
        Optional<CartItem> cartItemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (cartItemToRemove.isPresent()) {
            CartItem item = cartItemToRemove.get();
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            log.error("Error removing item from cart: {}", itemId);
            throw new ContentNotFoundException("Cart item not found with id: " + itemId);
        }
    }

    public CartItem findById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ContentNotFoundException("Cart not found with id: " + itemId));
    }

    public CartItem updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }
}