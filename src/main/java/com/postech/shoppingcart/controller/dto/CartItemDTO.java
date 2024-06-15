package com.postech.shoppingcart.controller.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@RequiredArgsConstructor
public class CartItemDTO {

    private Long productId;
    private int quantity;
    private BigDecimal price;

}
