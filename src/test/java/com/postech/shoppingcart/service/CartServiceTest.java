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
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        cartItemDTO.setPrice(BigDecimal.valueOf(34.5));
        Cart cart = new Cart();
        cart.setId(cartId);

        // Mock
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemService.createOrUpdateCartItem(any(Cart.class), any(CartItemDTO.class)))
                .thenAnswer(invocation -> {
                    CartItem item = new CartItem();
                    item.setProductId(invocation.getArgument(1, CartItemDTO.class).getProductId());
                    item.setQuantity(invocation.getArgument(1, CartItemDTO.class).getQuantity());
                    item.setPrice(invocation.getArgument(1, CartItemDTO.class).getPrice());
                    return item;
                });
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartDTO result = cartService.addItemToCart(cartId, cartItemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(cartItemDTO, result.getItems().get(0));
        assertEquals(cartId, result.getId());
        assertEquals(result.getTotal(), cartItemDTO.getPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
    }

    /* Cart Items */

    @Test
    public void testUpdateItemQuantity_Success() {
        // Arrange
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;

        Cart cart = new Cart();
        cart.setId(cartId);

        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setQuantity(2); // Original quantity
        cartItem.setPrice(BigDecimal.valueOf(34.5));
        cartItem.setCart(cart);

        cart.getItems().add(cartItem); // Add the cart item to the cart

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemService.findById(itemId)).thenReturn(cartItem);
        when(cartItemService.updateCartItemQuantity(any(CartItem.class), eq(newQuantity)))
                .thenAnswer(invocation -> {
                    CartItem updatedItem = invocation.getArgument(0, CartItem.class);
                    updatedItem.setQuantity(newQuantity);
                    return updatedItem;
                });
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0, Cart.class);
            savedCart.getItems().set(0, cartItem);
            return savedCart;
        });

        // Act
        CartDTO result = cartService.updateItemQuantity(cartId, itemId, newQuantity);

        // Assert
        assertNotNull(result);
        assertEquals(newQuantity, result.getItems().get(0).getQuantity());
        verify(cartItemService, times(1)).updateCartItemQuantity(cartItem, newQuantity);
    }

    @Test
    public void testUpdateItemQuantity_CartNotFound() {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class,
                () -> cartService.updateItemQuantity(cartId, itemId, newQuantity));
    }

    @Test
    public void testUpdateItemQuantity_CartItemNotFound() {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;
        Cart cart = new Cart();
        cart.setId(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemService.findById(itemId)).thenThrow(new ContentNotFoundException("Cart item not found"));

        assertThrows(ContentNotFoundException.class,
                () -> cartService.updateItemQuantity(cartId, itemId, newQuantity));
    }

    @Test
    public void testUpdateItemQuantity_InvalidQuantity() {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = -1;

        Cart cart = new Cart();
        cart.setId(cartId);

        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setQuantity(1); // Original quantity
        cartItem.setPrice(BigDecimal.valueOf(34.5));
        cartItem.setCart(cart);

        cart.getItems().add(cartItem);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        assertThrows(IllegalArgumentException.class,
                () -> cartService.updateItemQuantity(cartId, itemId, newQuantity));


    }

    @Test
    public void testRemoveItemFromCart_Success() {
        // Arrange
        Long cartId = 1L;
        Long itemId = 101L;
        Cart cart = new Cart();
        cart.setId(cartId);
        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setCart(cart);
        cartItem.setPrice(BigDecimal.valueOf(34.5));
        cart.getItems().add(cartItem);

        Cart newCart = cart;
        newCart.setItems(new ArrayList<CartItem>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartItemService.removeItem(itemId, cart)).thenReturn(newCart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartDTO result = cartService.removeItemFromCart(cartId, itemId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty()); // Item should be removed
        verify(cartItemService, times(1)).removeItem(itemId, cart);
    }

    @Test
    public void testRemoveItemFromCart_CartNotFound() {
        Long cartId = 1L;
        Long itemId = 101L;

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> cartService.removeItemFromCart(cartId, itemId));
    }

    @Test
    public void testRemoveItemFromCart_ItemNotFound() {
        Long cartId = 1L;
        Long itemId = 101L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        doThrow(ContentNotFoundException.class).when(cartItemService).removeItem(itemId, cart);

        assertThrows(ContentNotFoundException.class, () -> cartService.removeItemFromCart(cartId, itemId));
    }

    @Test
    public void testRemoveItemFromCart_UnexpectedError() {
        Long cartId = 1L;
        Long itemId = 101L;
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        doThrow(RuntimeException.class).when(cartItemService).removeItem(itemId, cart);

        assertThrows(RuntimeException.class, () -> cartService.removeItemFromCart(cartId, itemId));
    }

    /**/
    @Test
    public void testDeleteCart_Success() {
        // Arrange
        Long cartId = 1L;

        // Act
        cartService.deleteCart(cartId);

        // Assert
        verify(cartRepository, times(1)).deleteById(cartId);
    }

    @Test
    public void testDeleteCart_CartNotFound() {
        // Arrange
        Long cartId = 1L;

        doThrow(EmptyResultDataAccessException.class).when(cartRepository).deleteById(cartId);

        // Act & Assert
        assertThrows(EmptyResultDataAccessException.class, () -> cartService.deleteCart(cartId));
    }

    @Test
    public void testDeleteCart_UnexpectedError() {
        // Arrange
        Long cartId = 1L;

        doThrow(RuntimeException.class).when(cartRepository).deleteById(cartId); // Simulate an error

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.deleteCart(cartId));
    }
}
