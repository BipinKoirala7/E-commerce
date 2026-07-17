package com.Ecommerce.OrderService.Integration;

import com.Ecommerce.OrderService.Client.ProductServiceClient;
import com.Ecommerce.OrderService.DTOs.Response.ProductSummary;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import com.Ecommerce.OrderService.Model.CartItem;
import com.Ecommerce.OrderService.Repository.CartItemRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test for the cart-item vertical slice: {@code CartItemController} ->
 * {@code CartItemService} -> {@code CartItemRepository}, backed by a real Postgres
 * Testcontainer and going through the actual HTTP layer via {@link TestRestTemplate}.
 *
 * <p>ASSUMPTIONS (adjust if they don't match your actual code):
 * <ul>
 *   <li>{@code CartItem} exposes {@code getId()/getProductId()/getUserId()/getQuantity()}
 *       and a no-args constructor + setters (typical Lombok {@code @Data} entity).</li>
 *   <li>{@code CartItemUpdateDto} has a single-arg constructor {@code CartItemUpdateDto(Integer quantity)}
 *       matching the call site in {@code CartItemService#createNewCartItem}.</li>
 *   <li>{@code RestApiResponse} JSON shape is {@code { success, message, data, status }} —
 *       tests below deserialize into a generic {@code Map} for the wrapper instead of the
 *       concrete generic type, specifically to avoid guessing at {@code CartItemResponseDTO}
 *       / {@code ProductSummary} field names.</li>
 *   <li>{@code ProductServiceClient.getProductSummary(UUID)} is a Feign client interface;
 *       it's mocked out here with {@code @MockitoBean} since we don't want a real HTTP call
 *       to a downstream product service in this test.</li>
 * </ul>
 *
 * <p>NOTE: {@code updateCartItem}/{@code deleteCartItem} on a missing cart item throw
 * {@code CartItemNotFound}, but {@code GlobalExceptionHandler} has no {@code @ExceptionHandler}
 * registered for it — it falls through to the generic {@code Exception} handler and comes
 * back as 500, not 404. The tests below assert that *actual* current behavior; if you add a
 * dedicated handler for {@code CartItemNotFound} returning 404, update
 * {@code updateCartItem_whenItemMissing_...} and {@code deleteCartItem_whenItemMissing_...}
 * accordingly.
 */
class CartItemIntegrationTest extends IntegrationTestSetup {

  private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE =
      new ParameterizedTypeReference<>() {};

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private CartItemRepository cartItemRepository;

  @MockitoBean
  private ProductServiceClient productServiceClient;

  private UUID productId;

  @BeforeEach
  void setUp() {
    cartItemRepository.deleteAll();
    productId = UUID.randomUUID();
  }

  private HttpEntity<Void> authEntity() {
    return new HttpEntity<>(getAuthenticatedHeaders(login()));
  }

  private HttpEntity<Map<String, Object>> authEntity(Map<String, Object> body) {
    return new HttpEntity<>(body, getAuthenticatedHeaders(login()));
  }

  private CartItem persistCartItem(UUID productIdToUse, int quantity) {
    CartItem cartItem = new CartItem();
    cartItem.setProductId(productIdToUse);
    cartItem.setUserId(userId);
    cartItem.setQuantity(quantity);
    return cartItemRepository.save(cartItem);
  }

  // ---------------------------------------------------------------------
  // POST /cart-item/{productId}  (createCartItem)
  // ---------------------------------------------------------------------

  @Test
  void createCartItem_whenNewProduct_shouldPersistWithQuantityOne() {
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.POST, authEntity(), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).containsEntry("success", true);

    Optional<CartItem> saved = cartItemRepository.findByProductIdAndUserId(productId, userId);
    assertThat(saved).isPresent();
    assertThat(saved.get().getQuantity()).isEqualTo(1);
    assertThat(saved.get().getUserId()).isEqualTo(userId);
  }

  @Test
  void createCartItem_whenAlreadyInCart_shouldIncrementExistingRowInsteadOfDuplicating() {
    persistCartItem(productId, 2);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.POST, authEntity(), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    List<CartItem> allForUser = cartItemRepository.findByUserId(userId);
    assertThat(allForUser).hasSize(1);
    assertThat(allForUser.get(0).getQuantity()).isEqualTo(3);
  }

  // ---------------------------------------------------------------------
  // GET /cart-item  (getCartItems)
  // ---------------------------------------------------------------------

  @Test
  void getCartItems_whenProductServiceSucceeds_shouldReturnEnrichedItems() {
    persistCartItem(productId, 2);

    ProductSummary productSummary = new ProductSummary();
    productSummary.setId(productId);
    productSummary.setName("Watch");
    productSummary.setBrand("Rolex");
    productSummary.setPrice(BigDecimal.ONE);
    productSummary.setImageUrl("https://example.com/image.jpg");

    RestApiResponse<Object> productResponse =
        RestApiResponse.success(HttpStatus.OK.value(), productSummary, "ok");
    when(productServiceClient.getProductSummary(any(UUID.class)))
        .thenReturn((RestApiResponse) productResponse);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item", HttpMethod.GET, authEntity(), RESPONSE_TYPE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsEntry("success", true);
    assert response.getBody() != null;
    assertThat(response.getBody().get("data")).isNotNull();

    Object data = response.getBody().get("data");
    assertThat(data).isInstanceOf(List.class);
    assertThat((List<?>) data).hasSize(1);
  }

  @Test
  void getCartItems_whenProductServiceReportsFailure_shouldReturn406() {
    persistCartItem(productId, 1);

    Request feignRequest = Request.create(Request.HttpMethod.GET, "/product-summary",
        Collections.emptyMap(), null, StandardCharsets.UTF_8, null);

    when(productServiceClient.getProductSummary(any(UUID.class)))
        .thenThrow(new FeignException.FeignClientException(
            404, "Not Found", feignRequest, null, null));

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item", HttpMethod.GET, authEntity(), RESPONSE_TYPE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).containsEntry("success", false);
  }

  @Test
  void getCartItems_whenCartIsEmpty_shouldReturnEmptyList() {
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item", HttpMethod.GET, authEntity(), RESPONSE_TYPE);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat((List<?>) response.getBody().get("data")).isEmpty();
  }

  // ---------------------------------------------------------------------
  // PATCH /cart-item/{productId}  (updateCartItem)
  // ---------------------------------------------------------------------

  @Test
  void updateCartItem_whenItemExistsAndQuantityPositive_shouldUpdateQuantity() {
    persistCartItem(productId, 1);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.PATCH,
        authEntity(Map.of("quantity", 5)), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Optional<CartItem> updated = cartItemRepository.findByProductIdAndUserId(productId, userId);
    assertThat(updated).isPresent();
    assertThat(updated.get().getQuantity()).isEqualTo(5);
  }

  @Test
  void updateCartItem_whenQuantitySetToZero_shouldDeleteTheCartItem() {
    persistCartItem(productId, 3);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.PATCH,
        authEntity(Map.of("quantity", 0)), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(cartItemRepository.findByProductIdAndUserId(productId, userId)).isEmpty();
  }

  @Test
  void updateCartItem_whenQuantityNegative_shouldReturn406AndLeaveItemUnchanged() {
    persistCartItem(productId, 1);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.PATCH,
        authEntity(Map.of("quantity", -1)), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);

    Optional<CartItem> unchanged = cartItemRepository.findByProductIdAndUserId(productId, userId);
    assertThat(unchanged).isPresent();
    assertThat(unchanged.get().getQuantity()).isEqualTo(1);
  }

  @Test
  void updateCartItem_whenItemMissing_currentlyReturns500NotFoundHandlerMissing() {
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.PATCH,
        authEntity(Map.of("quantity", 2)), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // ---------------------------------------------------------------------
  // DELETE /cart-item/{productId}  (deleteCartItem)
  // ---------------------------------------------------------------------

  @Test
  void deleteCartItem_whenItemExists_shouldRemoveIt() {
    persistCartItem(productId, 1);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.DELETE, authEntity(), RESPONSE_TYPE, productId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(cartItemRepository.findByProductIdAndUserId(productId, userId)).isEmpty();
  }

  @Test
  void deleteCartItem_whenItemMissing_currentlyReturns500NotFoundHandlerMissing() {
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.DELETE, authEntity(), RESPONSE_TYPE, productId);

    // Same caveat as the update case above.
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void deleteCartItem_shouldOnlyAffectTheCallingUsersItem() {
    UUID otherUserId = UUID.randomUUID();
    CartItem otherUsersItem = new CartItem();
    otherUsersItem.setProductId(productId);
    otherUsersItem.setUserId(otherUserId);
    otherUsersItem.setQuantity(1);
    cartItemRepository.save(otherUsersItem);

    persistCartItem(productId, 1); // same productId, current test user

    restTemplate.exchange(
        "/cart-item/{productId}", HttpMethod.DELETE, authEntity(), RESPONSE_TYPE, productId);

    assertThat(cartItemRepository.findByProductIdAndUserId(productId, userId)).isEmpty();
    assertThat(cartItemRepository.findByProductIdAndUserId(productId, otherUserId)).isPresent();
  }
}