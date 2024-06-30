package com.postech.shoppingcart.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class CartItemDTO {

    private Long id;
    @NotNull
    private Long productId;
    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;
    @NotNull
    private BigDecimal price;

}
