package com.postech.shoppingcart.controller;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.mapper.CartMapper;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import com.postech.shoppingcart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.PermitAll;
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

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
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
            CartDTO createdCart = cartService.createCart(cartDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCart);
        }catch (Exception e){
            log.error("Error creating Cart ", e);
            throw e;
        }
    }

    @Operation(summary = "Request for find a Cart", responses = {
            @ApiResponse(description = "The cart found", responseCode = "200", content = @Content(schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(description = "Cart does not exists", responseCode = "204", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
    })
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long cartId) {
        try {
            CartDTO cartDTO = cartService.getCart(cartId);
            return ResponseEntity.ok(cartDTO);
        } catch (Exception e) {
            log.error("Error getting cart ", e);
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Delete a Cart", responses = {
            @ApiResponse(description = "The cart was deleted", responseCode = "204", content = @Content(schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(description = "Cart does not exists", responseCode = "204", content = @Content(schema = @Schema(type = "string", example = "Campos inválidos ou faltando"))),
    })
    @DeleteMapping("/{cartId}")
    //@PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {

        try {
            cartService.deleteCart(cartId);
        } catch (Exception e) {
            log.error("Error deleting cart ", e);
            throw new RuntimeException(e);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItem(@PathVariable Long cartId, @RequestBody CartItemDTO request) {
        try {
            CartDTO updatedCart = cartService.addItemToCart(cartId, request);
            return ResponseEntity.ok(updatedCart); // Return the updated cart (optional)
        } catch (Exception e) {
            log.error("Error adding item", e);
            throw new RuntimeException(e);
        }
    }

}

