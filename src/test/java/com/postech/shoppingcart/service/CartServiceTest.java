package com.postech.shoppingcart.service;

import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.model.CartItem;
import com.postech.shoppingcart.repository.CartItemRepository;
import com.postech.shoppingcart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;



    @InjectMocks
    private CartService cartService;

    @Test
    public void testCreateCart() {
        Cart cart = new Cart();
        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotal(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO result = cartService.createCart(cartDTO);

        assertNotNull(result);
        //assertEquals(cart.getStatus(), result.getStatus());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
    @Test
    public void testGetCart_Success() {
        Long cartId = 1L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCart(cartId);

        assertNotNull(result);
        assertEquals(cartId, result.getId());
    }

    @Test
    public void testGetCart_NotFound() {
        Long cartId = 1L;

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> cartService.getCart(cartId));
    }

    @Test
    public void testAddItemToCart() {
        // Arrange
        Long cartId = 1L;
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(101L);
        cartItemDTO.setQuantity(2);
        Cart cart = new Cart();
        cart.setId(cartId);

        // Mock
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemService.createOrUpdateCartItem(any(Cart.class), any(CartItemDTO.class)))
                .thenAnswer(invocation -> {
                    CartItem item = new CartItem();
                    item.setProductId(invocation.getArgument(1, CartItemDTO.class).getProductId());
                    item.setQuantity(invocation.getArgument(1, CartItemDTO.class).getQuantity());
                    return item;
                });
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartDTO result = cartService.addItemToCart(cartId, cartItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }
}
