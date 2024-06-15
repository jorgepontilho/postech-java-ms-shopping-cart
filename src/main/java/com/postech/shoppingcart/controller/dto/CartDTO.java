package com.postech.shoppingcart.controller.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@RequiredArgsConstructor
@ToString
public class CartDTO {

    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    //private double subtotal;
    //private double taxes;
    private BigDecimal total;

}