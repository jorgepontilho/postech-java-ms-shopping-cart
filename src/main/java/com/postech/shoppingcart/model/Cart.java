package com.postech.shoppingcart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    private Long id;
    @OneToMany(mappedBy = "cart")
    private List<CartItem> items = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    // ... other fields (e.g., subtotal, taxes, total)
}