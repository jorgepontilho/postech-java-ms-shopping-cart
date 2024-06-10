package com.postech.shoppingcart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cart cart;
    private Long productId;
    private int quantity;
    // ... other fields (e.g., price, name, image)
}
