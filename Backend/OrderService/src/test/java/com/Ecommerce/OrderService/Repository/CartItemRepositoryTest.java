package com.Ecommerce.OrderService.Repository;

import com.Ecommerce.OrderService.Model.CartItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CartItemRepositoryTest {

  @Autowired
  private CartItemRepository cartItemRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
      .withUsername("postgres")
      .withPassword("postgres")
      .withDatabaseName("test")
      .withReuse(true);

  private UUID userId;
  private UUID productId;
  private CartItem cartItem;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    productId = UUID.randomUUID();

    cartItem = new CartItem();
    cartItem.setUserId(userId);
    cartItem.setProductId(productId);
    cartItem.setQuantity(1);

    cartItem = entityManager.persistFlushFind(cartItem);
  }

  @AfterAll
  static void tearDown() {
    postgreSQLContainer.stop();
  }

  @Nested
  class FindByUserId {

    @Test
    void returnsAllCartItemsForGivenUser() {
      CartItem secondItem = new CartItem();
      secondItem.setUserId(userId);
      secondItem.setProductId(UUID.randomUUID());
      secondItem.setQuantity(2);
      entityManager.persistFlushFind(secondItem);

      CartItem otherUsersItem = new CartItem();
      otherUsersItem.setUserId(UUID.randomUUID());
      otherUsersItem.setProductId(UUID.randomUUID());
      otherUsersItem.setQuantity(5);
      entityManager.persistFlushFind(otherUsersItem);

      List<CartItem> result = cartItemRepository.findByUserId(userId);

      assertThat(result)
          .hasSize(2)
          .extracting(CartItem::getId)
          .containsExactlyInAnyOrder(cartItem.getId(), secondItem.getId());
    }

    @Test
    void returnsEmptyListWhenUserHasNoCartItems() {
      List<CartItem> result = cartItemRepository.findByUserId(UUID.randomUUID());

      assertThat(result).isEmpty();
    }
  }

  @Nested
  class FindByProductIdAndUserId {

    @Test
    void returnsCartItemWhenMatchExists() {
      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(productId, userId);

      assertThat(result).isPresent();
      assertThat(result.get().getId()).isEqualTo(cartItem.getId());
    }

    @Test
    void returnsEmptyWhenProductIdDoesNotMatch() {
      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(UUID.randomUUID(), userId);

      assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyWhenUserIdDoesNotMatch() {
      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(productId, UUID.randomUUID());

      assertThat(result).isEmpty();
    }
  }

  @Nested
  class FindByIdAndUserId {

    @Test
    void returnsCartItemWhenMatchExists() {
      Optional<CartItem> result = cartItemRepository.findByIdAndUserId(cartItem.getId(), userId);

      assertThat(result).isPresent();
      assertThat(result.get().getProductId()).isEqualTo(productId);
    }

    @Test
    void returnsEmptyWhenIdDoesNotMatch() {
      Optional<CartItem> result = cartItemRepository.findByIdAndUserId(UUID.randomUUID(), userId);

      assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyWhenUserIdDoesNotMatch() {
      Optional<CartItem> result = cartItemRepository.findByIdAndUserId(cartItem.getId(), UUID.randomUUID());

      assertThat(result).isEmpty();
    }
  }

  @Nested
  class UpdateQuantityByIdAndUserId {

    @Test
    void updatesQuantityWhenMatchExists() {
      cartItemRepository.updateQuantityByIdAndUserId(9, productId, userId);
      entityManager.clear();

      CartItem updated = cartItemRepository.findByProductIdAndUserId(productId, userId).orElseThrow();
      assertThat(updated.getQuantity()).isEqualTo(9);
    }

    @Test
    void doesNothingWhenNoMatchingProductForUser() {
      cartItemRepository.updateQuantityByIdAndUserId(9, UUID.randomUUID(), userId);

      entityManager.clear();

      CartItem unchanged = cartItemRepository.findByProductIdAndUserId(productId, userId).orElseThrow();
      assertThat(unchanged.getQuantity()).isEqualTo(1);
    }

    @Test
    void doesNothingWhenNoMatchingUserForProduct() {
      cartItemRepository.updateQuantityByIdAndUserId(9, productId, UUID.randomUUID());

      entityManager.clear();

      CartItem unchanged = cartItemRepository.findByProductIdAndUserId(productId, userId).orElseThrow();
      assertThat(unchanged.getQuantity()).isEqualTo(1);
    }
  }

  @Nested
  class ExistsByProductIdAndUserId {

    @Test
    void returnsTrueWhenMatchExists() {
      boolean exists = cartItemRepository.existsByProductIdAndUserId(productId, userId);

      assertThat(exists).isTrue();
    }

    @Test
    void returnsFalseWhenProductIdDoesNotMatch() {
      boolean exists = cartItemRepository.existsByProductIdAndUserId(UUID.randomUUID(), userId);

      assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseWhenUserIdDoesNotMatch() {
      boolean exists = cartItemRepository.existsByProductIdAndUserId(productId, UUID.randomUUID());

      assertThat(exists).isFalse();
    }
  }

  @Nested
  class DeleteByProductIdAndUserId {

    @Test
    void deletesCartItemWhenMatchExists() {
      cartItemRepository.deleteByProductIdAndUserId(productId, userId);
      entityManager.flush();
      entityManager.clear();

      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(productId, userId);
      assertThat(result).isEmpty();
    }

    @Test
    void doesNothingWhenNoMatchingProductForUser() {
      cartItemRepository.deleteByProductIdAndUserId(UUID.randomUUID(), userId);
      entityManager.flush();
      entityManager.clear();

      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(productId, userId);
      assertThat(result).isPresent();
    }

    @Test
    void doesNothingWhenNoMatchingUserForProduct() {
      cartItemRepository.deleteByProductIdAndUserId(productId, UUID.randomUUID());
      entityManager.flush();
      entityManager.clear();

      Optional<CartItem> result = cartItemRepository.findByProductIdAndUserId(productId, userId);
      assertThat(result).isPresent();
    }
  }
}