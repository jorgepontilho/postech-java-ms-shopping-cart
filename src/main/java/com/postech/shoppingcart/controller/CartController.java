package com.postech.shoppingcart.controller;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<?> createCart(HttpServletRequest request, @Valid @RequestBody CartDTO cartDTO) {
        try {
            log.info("Creating cart for user: {} ", cartDTO.getUserId());
            if (request.getAttribute("error") != null) {
                return ResponseEntity.status((HttpStatusCode) request.getAttribute("error_code"))
                        .body(request.getAttribute("error"));
            }
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
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {

        log.info("Deleting cart {}", cartId);
        try {
            cartService.deleteCart(cartId);
        }  catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Add new item to cart", responses = {
            @ApiResponse(description = "The cart item was added", responseCode = "200", content = @Content(schema = @Schema(implementation = CartDTO.class))),
            @ApiResponse(description = "Cart does not exists", responseCode = "204", content = @Content(schema = @Schema(type = "string", example = "Carrinho não encontrado"))),
    })
    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItem(@PathVariable Long cartId, @Valid @RequestBody CartItemDTO request) {
        try {

            CartDTO updatedCart = cartService.addItemToCart(cartId, request);
            return ResponseEntity.ok(updatedCart);
        } catch (ContentNotFoundException e) {
            log.error("Error adding item, cart not found {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error adding item", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        try {
            CartDTO updatedCart = cartService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(updatedCart);
        } catch (ContentNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error removing item from cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while removing the item from the cart.");
        }
    }


    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<?> updateItemQuantity( @PathVariable Long cartId, @PathVariable Long itemId, @RequestParam @Min(value = 0) int quantity) {
        try {
            if (quantity > 0) {
                CartDTO updatedCart = cartService.updateItemQuantity(cartId, itemId, quantity);
                return ResponseEntity.ok(updatedCart);
            } else {
                log.error("invalid quantity [{}] for cart {}", quantity, cartId);
                return ResponseEntity.badRequest().build();
            }

        } catch (ContentNotFoundException e) {
            log.error("Error adding item, cart not found {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating item quantity: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}

