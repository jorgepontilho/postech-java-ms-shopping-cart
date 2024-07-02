package com.postech.shoppingcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.shoppingcart.TestUtils;
import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.controller.dto.CartItemDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class AddItemsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;


    @Test
    public void testAddItemToCart_Success() throws Exception {
        Long cartId = 1L;
        CartItemDTO cartItemDTO = TestUtils.createTestCartItemDTO(1L, 2, BigDecimal.valueOf(10.5));
        CartDTO expectedCartDTO = TestUtils.createTestCartDTO();
        expectedCartDTO.setItems(new ArrayList<CartItemDTO>());
        expectedCartDTO.getItems().add(cartItemDTO);

        when(cartService.addItemToCart(cartId, cartItemDTO)).thenReturn(expectedCartDTO);

        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(cartItemDTO.getProductId()))
                .andExpect(jsonPath("$.items[0].quantity").value(cartItemDTO.getQuantity()))
                .andExpect(jsonPath("$.items[0].price").value(cartItemDTO.getPrice()));
    }

    @Test
    public void testAddItemToCart_CartNotFound() throws Exception {
        Long cartId = 1L;
        CartItemDTO cartItemDTO = TestUtils.createTestCartItemDTO();

        doThrow(ContentNotFoundException.class).when(cartService).addItemToCart(cartId, cartItemDTO);

        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddItemToCart_InvalidInput() throws Exception {
        Long cartId = 1L;
        CartItemDTO cartItemDTO = new CartItemDTO();
        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddItemToCart_UnexpectedError() throws Exception {
        Long cartId = 1L;
        CartItemDTO cartItemDTO = TestUtils.createTestCartItemDTO(1L, 2, BigDecimal.valueOf(10.5));

        doThrow(RuntimeException.class).when(cartService).addItemToCart(cartId, cartItemDTO);

        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemDTO)))
                .andExpect(status().isInternalServerError());
    }

    /* REMOVE */
    @Test
    public void testRemoveItemFromCart_Success() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;
        CartDTO updatedCart = new CartDTO();

        when(cartService.removeItemFromCart(cartId, itemId)).thenReturn(updatedCart);

        mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", cartId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").doesNotExist()); // Expect no items after removal
    }

    @Test
    public void testRemoveItemFromCart_CartNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;

        when(cartService.removeItemFromCart(cartId, itemId)).thenThrow(ContentNotFoundException.class);

        mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", cartId, itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveItemFromCart_ItemNotFound() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;
        CartDTO existingCart = TestUtils.createTestCartDTO();

        when(cartService.removeItemFromCart(cartId, itemId)).thenThrow(new ContentNotFoundException("Item not found"));

        mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", cartId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(existingCart)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRemoveItemFromCart_UnexpectedError() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;

        when(cartService.removeItemFromCart(cartId, itemId)).thenThrow(new RuntimeException("Some error"));

        mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", cartId, itemId))
                .andExpect(status().isInternalServerError());
    }

    /* Update Items */
    @Test
    public void testUpdateItemQuantity_Success() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;
        CartDTO updatedCartDTO = TestUtils.createTestCartDTO();
        updatedCartDTO.setItems(TestUtils.createTestCartItemDTOs());
        updatedCartDTO.getItems().get(0).setQuantity(newQuantity);

        when(cartService.updateItemQuantity(cartId, itemId, newQuantity)).thenReturn(updatedCartDTO);

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}?quantity={quantity}", cartId, itemId, newQuantity))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(newQuantity));
    }

    @Test
    public void testUpdateItemQuantity_CartNotFound() throws Exception {
        // Arrange
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;

        when(cartService.updateItemQuantity(cartId, itemId, newQuantity))
                .thenThrow(ContentNotFoundException.class);

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}?quantity={quantity}", cartId, itemId, newQuantity))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItemQuantity_ItemNotFound() throws Exception {
        // Arrange
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;

        when(cartService.updateItemQuantity(cartId, itemId, newQuantity))
                .thenThrow(new ContentNotFoundException("Item not found"));

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}?quantity={quantity}", cartId, itemId, newQuantity))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItemQuantity_InvalidQuantity() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 0;

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}?quantity={quantity}", cartId, itemId, newQuantity))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateItemQuantity_UnexpectedError() throws Exception {
        Long cartId = 1L;
        Long itemId = 101L;
        int newQuantity = 3;

        when(cartService.updateItemQuantity(cartId, itemId, newQuantity))
                .thenThrow(new RuntimeException("Some error"));

        mockMvc.perform(put("/carts/{cartId}/items/{itemId}?quantity={quantity}", cartId, itemId, newQuantity))
                .andExpect(status().isInternalServerError());
    }
}