package com.postech.shoppingcart.controller;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.mapper.CartMapper;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @Autowired
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @PostMapping
    @Operation(summary = "Request for create a Cart", responses = {
            @ApiResponse(description = "The new cart was created", responseCode = "201", content = @Content(schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(description = "Cart Invalid", responseCode = "400", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
            @ApiResponse(description = "User invalid", responseCode = "404", content = @Content(schema = @Schema(type = "string", example = "Usuário não encontrado."))),
    })
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO) {
        try {
            log.info("create cart to user: {}", cartDTO.getUserId());
            Cart createdCart = cartService.createCart(cartMapper.toEntity(cartDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(cartMapper.toDTO(createdCart));
        }catch (Exception e){
            throw e;
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long cartId) {
        Cart cart = cartService.getCart(cartId);
        CartDTO cartDTO = cartMapper.toDTO(cart);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/{cartId}")
    //@PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        // Retrieve cart from the database
        Cart cart = cartService.getCart(cartId);

        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cartId}/items")
    public void addItem(@PathVariable String cartId, @RequestBody CartDTO request) {
        return;
    }

}

