package com.postech.shoppingcart.service;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.mapper.CartMapper;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import com.postech.shoppingcart.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemService cartItemService;


    public CartDTO createCart(CartDTO cartDTO) {
        try {
            Cart newCart = CartMapper.INSTANCE.toCart(cartDTO);

            Cart savedCart = cartRepository.save(newCart);
            return CartMapper.INSTANCE.toCartDTO(savedCart);

        } catch (Exception e) {
            log.error("Error creating cart: {}", e.getMessage());
            throw e;
        }
    }

    public CartDTO getCart(Long cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ContentNotFoundException("Cart not found"));
            return CartMapper.INSTANCE.toCartDTO(cart);
        } catch (Exception e) {
            log.error("Error getting cart: {}", e.getMessage());
            throw e;
        }
    }

    public CartDTO addItemToCart(Long cartId, CartItemDTO cartItemDTO) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ContentNotFoundException("Cart not found with id: " + cartId));

            // Validate cartItemDTO ( product exists)
            CartItem cartItem = cartItemService.createOrUpdateCartItem(cart, cartItemDTO);

            cart.getItems().add(cartItem);
            // Recalculate cart total
            cart.setTotal(calculateCartTotal(cart.getItems()));
            cart = cartRepository.save(cart);

            return CartMapper.INSTANCE.toCartDTO(cart);
        } catch (Exception e) {
            log.error("Unexpected error adding item to cart: {}", e.getMessage());
            throw e;
        }
    }

    public CartDTO updateItemQuantity(Long cartId, Long itemId, int newQuantity) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ContentNotFoundException("Cart not found with id: " + cartId));

            CartItem cartItem = cartItemService.findById(itemId);

            if (newQuantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            cartItem = cartItemService.updateCartItemQuantity(cartItem, newQuantity);
            
            cart.setTotal(calculateCartTotal(cart.getItems()));

            cart = cartRepository.save(cart);

            return CartMapper.INSTANCE.toCartDTO(cart);
        } catch (ContentNotFoundException e) {
            log.error("Error updating item quantity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating item quantity: {}", e.getMessage());
            throw e;
        }

    }

    public CartDTO removeItemFromCart(Long cartId, Long itemId) {
        try {
            log.info("Removing item from cart: {}", cartId);
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ContentNotFoundException("Cart not found with id: " + cartId));

            cart = cartItemService.removeItem(itemId, cart);

            // Recalculate cart total
            cart.setTotal(calculateCartTotal(cart.getItems()));

            cart = cartRepository.save(cart);

            return CartMapper.INSTANCE.toCartDTO(cart);
        } catch (ContentNotFoundException e) {
            log.error("Error removing item from cart: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error removing item from cart: {}", e.getMessage());
            throw e;
        }
    }


    public void deleteCart(Long cartId) {
        try {
            cartRepository.deleteById(cartId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Could not find item to delete: {}, {}",cartId, e.getMessage());
            throw e;
        }catch (Exception e) {
            log.error("Error deleting cart: {}, {}",cartId, e.getMessage());
            throw e;
        }
    }

    private BigDecimal calculateCartTotal(List<CartItem> items) {
        try {
            return items.stream()
                    .map(this::calculateItemTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            log.error("Unexpected error calculating cart total: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private BigDecimal calculateItemTotal(CartItem item) {
        BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
        return item.getPrice().multiply(quantity);
    }
}
