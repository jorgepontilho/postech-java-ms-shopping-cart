package com.postech.shoppingcart.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class CartDTO {

    private Long id;
    @NotNull
    private Long userId;
    private List<CartItemDTO> items;
    //private double subtotal;
    //private double taxes;
    private BigDecimal total;

}