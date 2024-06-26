package com.postech.shoppingcart.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@RequiredArgsConstructor
public class CartItemDTO {

    @NotNull
    private Long productId;
    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;
    @NotNull
    private BigDecimal price;

}
