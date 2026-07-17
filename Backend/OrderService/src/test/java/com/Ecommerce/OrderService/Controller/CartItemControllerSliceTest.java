package com.Ecommerce.OrderService.Controller;

import com.Ecommerce.OrderService.Config.AppConfig;
import com.Ecommerce.OrderService.Service.CartItemService;
import com.Ecommerce.OrderService.Service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(AppConfig.class)
public class CartItemControllerSliceTest {

  @MockitoBean
  private CartItemService cartItemService;

  @MockitoBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID productId;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
  }

  @Nested
  class CreateClassItem {

    @Test
    void createCartItem() throws Exception {
      mockMvc.perform(post("/cart-item/" + productId))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("Successfully Created Cart Item."))
          .andExpect(jsonPath("$.statusCode").value(201))
          .andExpect(jsonPath("$.data").doesNotExist());
    }
  }
}
