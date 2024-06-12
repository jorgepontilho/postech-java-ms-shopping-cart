package com.postech.shoppingcart.controller.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CartDTO {

    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
    private double subtotal;
    private double taxes;
    private double total;

}