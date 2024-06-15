package com.postech.shoppingcart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cart cart;
    private Long productId;
    private int quantity;
    private BigDecimal price;
}
