package com.Ecommerce.OrderService.Service;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import com.Ecommerce.OrderService.Client.ProductServiceClient;
import com.Ecommerce.OrderService.DTOs.Request.CartItemUpdateDto;
import com.Ecommerce.OrderService.Exception.TokenAuthenticationException;
import com.Ecommerce.OrderService.Mapper.CartItemMapper;
import com.Ecommerce.OrderService.Model.CartItem;
import com.Ecommerce.OrderService.Repository.CartItemRepository;
import com.Ecommerce.OrderService.Security.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

  @Mock
  private ProductServiceClient productServiceClient;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private CartItemMapper cartItemMapper;

  private MockedStatic<SecurityUtils> mockedSecurityUtils;

  @Spy
  @InjectMocks
  private CartItemService underTest;

  @Captor
  ArgumentCaptor<CartItem> captorCartItem;

  @BeforeEach
  void setup() {
    mockedSecurityUtils = mockStatic(SecurityUtils.class);
  }

  @AfterEach
  void tearDown() {
    mockedSecurityUtils.close();
  }

  @Nested
  class CreateNewCartItem {

    @Test
    void createNewCartItem() {
      UUID productId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CartItem newCartItem = new CartItem();
      newCartItem.setId(UUID.randomUUID());
      newCartItem.setUserId(userId);
      newCartItem.setProductId(productId);
      newCartItem.setQuantity(1);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());
      when(cartItemMapper.toCartItemEntity(productId)).thenReturn(newCartItem);

      underTest.createNewCartItem(productId);

      verify(cartItemRepository, times(1)).findByProductIdAndUserId(productId, userId);
      verify(underTest, never()).updateCartItemQuantity(any(), any());
      verify(cartItemMapper, times(1)).toCartItemEntity(productId);
      verify(cartItemRepository, times(1)).save(captorCartItem.capture());

      assertEquals(userId, captorCartItem.getValue().getUserId());
      assertEquals(productId, captorCartItem.getValue().getProductId());
      assertEquals(newCartItem.getQuantity(), captorCartItem.getValue().getQuantity());
    }

    @Test
    void createNewCartItem_whenCartItemWithGivenProductIdIsAlreadyPresent() {
      // Arrange
      UUID productId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CartItem cartitem = new CartItem();
      cartitem.setId(UUID.randomUUID());
      cartitem.setUserId(userId);
      cartitem.setProductId(productId);
      cartitem.setQuantity(1);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartitem));
      doNothing().when(underTest).updateCartItemQuantity(any(), any());

      // Act
      underTest.createNewCartItem(productId);

      // Assert
      verify(cartItemRepository, times(1)).findByProductIdAndUserId(productId, userId);
      verify(underTest, times(1)).updateCartItemQuantity(productId, new
      CartItemUpdateDto(cartitem.getQuantity() + 1));
    }

    @Test
    void createNewCartItem_whenUserIsNotAuthenticated(){
      // Arrange
      UUID productId = UUID.randomUUID();
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenThrow(new TokenAuthenticationException("Authentication is required to access this resource"));

      // Act
      TokenAuthenticationException e = assertThrows(TokenAuthenticationException.class, () -> underTest.createNewCartItem(productId));

      // Assert
      assertEquals("Authentication is required to access this resource", e.getMessage());
      verify(cartItemRepository, never()).findByProductIdAndUserId(any(), any());

    }

    @Test
    void createNewCartItem_whenProductIdIsNull() {
      // Act & Assert
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
          () -> underTest.createNewCartItem(null));
      assertEquals("Product Id is null", e.getMessage());
    }

  }
}
