package com.postech.shoppingcart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.shoppingcart.TestUtils;
import com.postech.shoppingcart.controller.dto.CartDTO;
import com.postech.shoppingcart.exception.ContentNotFoundException;
import com.postech.shoppingcart.mapper.CartMapper;
import com.postech.shoppingcart.model.Cart;
import com.postech.shoppingcart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private CartMapper cartMapper;

    @Test
    public void testCreateCart_Success() throws Exception {
        
        CartDTO cartDTOinput = TestUtils.createTestCartDTO();
        Cart savedCart = TestUtils.createTestCart();
        CartDTO cartDTOoutput = cartDTOinput;
        cartDTOoutput.setId(1L);

        when(cartService.createCart(cartDTOinput)).thenReturn(cartDTOoutput);
        when(cartMapper.toCartDTO(savedCart)).thenReturn(cartDTOoutput);

        // Act & Assert
        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartDTOinput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(cartDTOoutput.getTotal()))
                .andExpect(jsonPath("$.items").value(cartDTOoutput.getItems()));
    }

    @Test
    public void testCreateCart_Fail() throws Exception {
        CartDTO cartDTOinput = TestUtils.createTestCartDTO();
        cartDTOinput.setUserId(null);
        Cart savedCart = TestUtils.createTestCart();
        CartDTO cartDTOoutput = cartDTOinput;
        cartDTOoutput.setId(1L);

        when(cartService.createCart(cartDTOinput)).thenReturn(cartDTOoutput);
        when(cartMapper.toCartDTO(savedCart)).thenReturn(cartDTOoutput);

        // Act & Assert
        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartDTOinput)))
                .andExpect(content().string("Campos inválidos ou faltando: userId"));

    }

    @Test
    public void handleContentNotFoundException() throws Exception {
        when(cartService.getCart(anyLong())).thenThrow(new ContentNotFoundException("Cart not found"));

        mockMvc.perform(get("/carts/123"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void handleMethodArgumentNotValidException() throws Exception {
        CartDTO cartDTOinput = TestUtils.createTestCartDTO();
        cartDTOinput.setUserId(null);

        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartDTOinput)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Campos inválidos ou faltando: userId"));

    }

}