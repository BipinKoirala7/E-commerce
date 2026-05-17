package com.Ecommerce.OrderService.Repository;

import com.Ecommerce.OrderService.Model.Order;
import com.Ecommerce.OrderService.Model.OrderItem;
import com.Ecommerce.OrderService.Model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  List<Order> findByUserId(UUID userId);

  Optional<Order> findByIdAndUserId(UUID id, UUID userId);

  @Modifying
  @Query(value = "UPDATE orders SET order_status= :status WHERE order_id= :order_id AND user_id= :user_id", nativeQuery = true)
  @Transactional
  void updateOrderStatus(@Param("status") OrderStatus orderStatus, @Param("order_id") UUID orderId, @Param("user_id") UUID user_id);

  boolean existsByIdAndUserId(UUID id, UUID userId);

  int deleteByIdAndUserId(UUID id, UUID userId);
}
