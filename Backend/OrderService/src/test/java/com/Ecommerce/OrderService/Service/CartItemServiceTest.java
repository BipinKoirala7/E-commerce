package com.Ecommerce.OrderService.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.Ecommerce.OrderService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.ProductSummary;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import feign.FeignException;
import feign.Request;
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

  @Captor
  ArgumentCaptor<RestApiResponse<ProductSummary>> captorRestApiResponse;

  private UUID userId;
  private UUID productId;
  private CartItem cartItem1;
  private CartItem cartItem2;
  private CartItem cartItem3;
  private ProductSummary productSummary1;
  private ProductSummary productSummary2;
  private ProductSummary productSummary3;
  private CartItemResponseDTO cartItemResponseDTO1;
  private CartItemResponseDTO cartItemResponseDTO2;
  private CartItemResponseDTO cartItemResponseDTO3;

  @BeforeEach
  void setup() {
    userId = UUID.randomUUID();
    productId = UUID.randomUUID();
    mockedSecurityUtils = mockStatic(SecurityUtils.class);

    cartItem1 = new CartItem();
    cartItem1.setId(UUID.randomUUID());
    cartItem1.setProductId(productId);
    cartItem1.setUserId(userId);
    cartItem1.setQuantity(1);

    cartItem2 = new CartItem();
    cartItem2.setId(UUID.randomUUID());
    cartItem2.setProductId(UUID.randomUUID());
    cartItem2.setUserId(userId);
    cartItem2.setQuantity(2);

    cartItem3 = new CartItem();
    cartItem3.setId(UUID.randomUUID());
    cartItem3.setProductId(productId);
    cartItem3.setUserId(userId);
    cartItem3.setQuantity(3);

    productSummary1 = new ProductSummary();
    productSummary1.setId(cartItem1.getProductId());
    productSummary2 = new ProductSummary();
    productSummary2.setId(cartItem2.getProductId());
    productSummary3 = new ProductSummary();
    productSummary3.setId(cartItem3.getProductId());

    cartItemResponseDTO1 = new CartItemResponseDTO();
    cartItemResponseDTO1.setId(cartItem1.getId());
    cartItemResponseDTO1.setQuantity(cartItem1.getQuantity());
    cartItemResponseDTO1.setProduct(productSummary1);

    cartItemResponseDTO2 = new CartItemResponseDTO();
    cartItemResponseDTO2.setId(cartItem2.getId());
    cartItemResponseDTO2.setProduct(productSummary2);
    cartItemResponseDTO2.setQuantity(cartItem2.getQuantity());
    cartItemResponseDTO2.setProduct(productSummary2);

    cartItemResponseDTO3 = new CartItemResponseDTO();
    cartItemResponseDTO3.setId(cartItem3.getId());
    cartItemResponseDTO3.setProduct(productSummary3);
    cartItemResponseDTO3.setQuantity(cartItem3.getQuantity());
    cartItemResponseDTO3.setProduct(productSummary3);
  }

  @AfterEach
  void tearDown() {
    mockedSecurityUtils.close();
  }

  @Nested
  class CreateNewCartItem {

    @Test
    void createNewCartItem() {
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.empty());
      when(cartItemMapper.toCartItemEntity(productId)).thenReturn(cartItem1);

      // Act
      underTest.createNewCartItem(productId);

      // Assert
      verify(cartItemRepository, times(1)).findByProductIdAndUserId(productId, userId);
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(2));
      verify(underTest, never()).updateCartItemQuantity(any(), any());
      verify(cartItemMapper, times(1)).toCartItemEntity(productId);
      verify(cartItemRepository, times(1)).save(captorCartItem.capture());

      assertEquals(userId, captorCartItem.getValue().getUserId());
      assertEquals(productId, captorCartItem.getValue().getProductId());
      assertEquals(cartItem1.getQuantity(), captorCartItem.getValue().getQuantity());
    }

    @Test
    void createNewCartItem_whenCartItemWithGivenProductIdIsAlreadyPresent() {
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByProductIdAndUserId(productId, userId)).thenReturn(Optional.of(cartItem1));
      doNothing().when(underTest).updateCartItemQuantity(any(), any());

      // Act
      underTest.createNewCartItem(productId);

      // Assert
      verify(cartItemRepository, times(1)).findByProductIdAndUserId(productId, userId);
      verify(underTest, times(1)).updateCartItemQuantity(productId, new CartItemUpdateDto(cartItem1.getQuantity() + 1));
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(1));
      verify(cartItemRepository, never()).save(any());

    }

    @Test
    void createNewCartItem_whenUserIsNotAuthenticated(){
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenThrow(new TokenAuthenticationException("Authentication is required to access this resource"));

      // Act
      TokenAuthenticationException e = assertThrows(TokenAuthenticationException.class, () -> underTest.createNewCartItem(productId));

      // Assert
      assertEquals("Authentication is required to access this resource", e.getMessage());
      verify(cartItemRepository, never()).findByProductIdAndUserId(any(), any());
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(1));
    }

    @Test
    void createNewCartItem_whenProductIdIsNull() {
      // Act & Assert
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
          () -> underTest.createNewCartItem(null));
      assertEquals("Product Id is null", e.getMessage());
    }
  }

  @Nested
  class GetAllCartItem {

    @Test
    void getAllCartItem_whenCartItemIsEmpty() {
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

      // Act
      List<CartItemResponseDTO> result = underTest.getAllCartItems();

      // Assert
      assertEquals(0, result.size());
      verify(cartItemRepository, times(1)).findByUserId(userId);
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(1));
    }

    @Test
    void getAllCartItem_whenCartItemHasOneItem() {
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.singletonList(cartItem1));
      when(productServiceClient.getProductSummary(cartItem1.getProductId())).thenReturn(RestApiResponse.success(200, productSummary1, "Product Summary Retrieval Success"));

      // Act
      List<CartItemResponseDTO> result = underTest.getAllCartItems();

      // Assert
      assertEquals(1, result.size());
      verify(cartItemRepository, times(1)).findByUserId(userId);
      verify(productServiceClient, times(1)).getProductSummary(cartItem1.getProductId());
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(1));
    }

    @Test
    void getAllCartItem_whenCartItemHasMultipleItems() {
      // Arrange
      List<CartItemResponseDTO> cartItemResponseDTOList = Arrays.asList(cartItemResponseDTO1, cartItemResponseDTO2, cartItemResponseDTO3);
      List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByUserId(userId)).thenReturn(cartItems);
      when(productServiceClient.getProductSummary(cartItem1.getProductId())).thenReturn(RestApiResponse.success(200, productSummary1, "Product Summary Retrieval Success"));
      when(productServiceClient.getProductSummary(cartItem2.getProductId())).thenReturn(RestApiResponse.success(200, productSummary2, "Product Summary Retrieval Success"));
      when(productServiceClient.getProductSummary(cartItem3.getProductId())).thenReturn(RestApiResponse.success(200, productSummary3, "Product Summary Retrieval Success"));

      // Act
      List<CartItemResponseDTO> result = underTest.getAllCartItems();

      // Assert
      AtomicInteger i = new AtomicInteger();
      result.forEach((item) -> {
        CartItemResponseDTO original = cartItemResponseDTOList.get(i.get());

        assertEquals(original.getId(), item.getId());
        assertEquals(original.getProduct().getId(), item.getProduct().getId());
        assertEquals(original.getQuantity(), item.getQuantity());

        i.getAndIncrement();
      });
      assertEquals(3, result.size());

      verify(cartItemRepository, times(1)).findByUserId(userId);
      mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId, times(1));
    }

    @Test
    void getAllCartItem_whenOneProductSummaryRetrievalFails(){
      // Arrange
      List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2, cartItem3);
      Request feignRequest = Request.create(Request.HttpMethod.GET, "/product-summary",
          Collections.emptyMap(), null, StandardCharsets.UTF_8, null);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByUserId(userId)).thenReturn(cartItems);
      when(productServiceClient.getProductSummary(cartItem1.getProductId())).thenReturn(RestApiResponse.success(200, productSummary1, "Product Summary Retrieval Success"));
      when(productServiceClient.getProductSummary(cartItem2.getProductId())).thenThrow(new FeignException.FeignClientException(400, "Product Summary Retrieval Failed", feignRequest, null, null));
      when(productServiceClient.getProductSummary(cartItem3.getProductId())).thenReturn(RestApiResponse.success(200, productSummary3, "Product Summary Retrieval Success"));

      // Act & Assert
      FeignException.FeignClientException feignException = assertThrows(FeignException.FeignClientException.class, () -> underTest.getAllCartItems());

      assertEquals("Product Summary Retrieval Failed", feignException.getMessage());
    }

    @Test
    void getAllCartItem_whenUserIsNotAuthenticated() {
      // Arrange
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenThrow(new TokenAuthenticationException("Authentication is required to access this resource"));

      // Act & Assert
      TokenAuthenticationException e = assertThrows(TokenAuthenticationException.class, () -> underTest.getAllCartItems());

      assertEquals("Authentication is required to access this resource", e.getMessage());
      verify(cartItemRepository, never()).findByUserId(any());
    }

    @Test
    void getAllCartItem_whenProductClientThrowsException() {
      // Arrange
      UUID userId = UUID.randomUUID();
      UUID productId = UUID.randomUUID();
      CartItem cartitem = new CartItem();
      cartitem.setId(UUID.randomUUID());
      cartitem.setUserId(userId);
      cartitem.setProductId(productId);
      cartitem.setQuantity(1);

      Request feignRequest = Request.create(Request.HttpMethod.GET, "/product-summary",
          Collections.emptyMap(), null, StandardCharsets.UTF_8, null);

      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
      when(cartItemRepository.findByUserId(userId)).thenReturn(java.util.List.of(cartitem));
      when(productServiceClient.getProductSummary(productId))
          .thenThrow(new FeignException.FeignClientException(400, "Product Summary Retrieval Failed", feignRequest, null, Collections.emptyMap()));

      // Act
      FeignException.FeignClientException e = assertThrows(FeignException.FeignClientException.class, () -> underTest.getAllCartItems());

      // Assert
      assertEquals("Product Summary Retrieval Failed", e.getMessage());
    }
  }
}
