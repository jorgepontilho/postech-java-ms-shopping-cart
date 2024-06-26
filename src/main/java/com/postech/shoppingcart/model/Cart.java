package com.postech.shoppingcart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    @OneToMany(mappedBy = "cart")
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal total;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public void removeItem(CartItem itemToRemove) {
        items.remove(itemToRemove);
        itemToRemove.setCart(null);
    }

}