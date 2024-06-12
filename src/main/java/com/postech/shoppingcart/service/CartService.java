package com.postech.shoppingcart.service;

import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;


    public Cart createCart(Cart cartId) {
        try {
            Cart cart = new Cart();

            //cart.setStatus("NEW");
           // cart.setCreatedAt(LocalDateTime.now());

            Cart savedCart = cartRepository.save(cart);
            return savedCart;

        } catch (DataIntegrityViolationException e) {
            log.error("Error creating cart: {}", e.getMessage());
            throw new DataConflictException("Error creating cart. Please check the data and try again.");
        } catch (Exception e) {
            log.error("Unexpected error creating cart: {}", e.getMessage());
            throw new InternalServerErrorException("An unexpected error occurred while creating the cart.");
        }
    }

    public Cart getCart(Long cartId) {
        throw new NotImplementedException();
        //                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }
    public void addItemToCart(String cartId, Long productId, int quantity) { throw new NotImplementedException(); }
    public void updateItemQuantity(String cartId, Long productId, int quantity) { throw new NotImplementedException(); }
    public void removeItemFromCart(String cartId, Long productId) { throw new NotImplementedException(); }
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
    public double calculateCartTotal(Cart cart) { throw new NotImplementedException(); }
}
