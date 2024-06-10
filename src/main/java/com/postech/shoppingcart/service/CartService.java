package com.postech.shoppingcart.service;

import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.repository.CartRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCart(String cartId) { throw new NotImplementedException(); }
    public Cart createCart(Cart cartId) { throw new NotImplementedException(); }
    public void addItemToCart(String cartId, Long productId, int quantity) { throw new NotImplementedException(); }
    public void updateItemQuantity(String cartId, Long productId, int quantity) { throw new NotImplementedException(); }
    public void removeItemFromCart(String cartId, Long productId) { throw new NotImplementedException(); }
    public double calculateCartTotal(Cart cart) { throw new NotImplementedException(); }
}
