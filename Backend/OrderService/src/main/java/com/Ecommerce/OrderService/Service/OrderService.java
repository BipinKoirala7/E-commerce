package com.Ecommerce.OrderService.Service;

import cn.hutool.core.lang.Snowflake;
import com.Ecommerce.OrderService.Client.ProductServiceClient;
import com.Ecommerce.OrderService.DTOs.Request.OrderCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderItemCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderUpdateDTO;
import com.Ecommerce.OrderService.DTOs.Response.*;
import com.Ecommerce.OrderService.Exception.EmptyProductsOrderCreationException;
import com.Ecommerce.OrderService.Exception.OrderNotFound;
import com.Ecommerce.OrderService.Exception.ZeroItemQuantityInOrderException;
import com.Ecommerce.OrderService.Mapper.OrderMapper;
import com.Ecommerce.OrderService.Model.Order;
import com.Ecommerce.OrderService.Model.OrderItem;
import com.Ecommerce.OrderService.Model.OrderStatus;
import com.Ecommerce.OrderService.Repository.OrderRepository;
import com.Ecommerce.OrderService.Security.SecurityUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles Order related services like creation,
 * fetch, update & deletion.
 *
 * @see ProductServiceClient
 * @see OrderRepository
 * @see OrderMapper
 * @see JwtService
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final ProductServiceClient productServiceClient;
  private final OrderRepository orderRepository;
  private final Snowflake snowflake;
  private final OrderMapper orderMapper;
  private final JwtService jwtService;

  @Transactional
  public void createNewOrder(@NonNull OrderCreateDTO orderCreateDTO) {
    log.info("Order Creation");

    validateOrderItems(orderCreateDTO.getOrderItems());

    Set<UUID> productIdsSet = orderCreateDTO.getOrderItems().stream().map(OrderItemCreateDTO::getProductId).collect(Collectors.toSet());
    Map<UUID, ProductSummary> productSummaryMap = fetchBatchProduct(productIdsSet);
    Order newOrder = createOrderInstance(orderCreateDTO, productSummaryMap);
    newOrder.setOrderNumber(snowflake.nextIdStr());

    this.orderRepository.save(newOrder);
    log.info("Order Creation Success");
  }

  public List<OrderListResponseDTO> getAllOrders() {
    log.info("Fetching all Orders");
    List<Order> orders = orderRepository.findByUserId(SecurityUtils.getCurrentUserId());

    return orders.stream().map(order -> {
      OrderListResponseDTO listResponseDTO = orderMapper.toListResponseDTO(order);
      listResponseDTO.setNoOfItems(order.getOrderItems().size());
      return listResponseDTO;
    }).toList();
  }

  public OrderDetailsResponseDTO getOrder(UUID orderId) {
    log.info("Fetching Order");

    Order order = orderRepository.findByIdAndUserId(orderId, SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> {
          log.warn("Fetching Order Failed - Order not found");
          return new OrderNotFound("Order with given id not found");
        });

    OrderDetailsResponseDTO responseDTO = orderMapper.toDetailsResponseDTO(order);
    responseDTO.setOrderItems(new ArrayList<>());

    Set<UUID> productIdsSet = order.getOrderItems().stream().map(OrderItem::getProductId).collect(Collectors.toSet());
    Map<UUID, ProductSummary> productSummaryMap = fetchBatchProduct(productIdsSet);

    for(OrderItem orderItem : order.getOrderItems()){
      responseDTO
          .getOrderItems()
          .add(new OrderItemResponseDTO(orderItem.getId(), productSummaryMap.get(orderItem.getProductId()), orderItem.getQuantity()));
    }

    log.info("Fetching Order Success");
    return responseDTO;
  }

  @Transactional
  public void updateOrder(@NotNull UUID orderId, @NonNull OrderUpdateDTO orderUpdateDTO) {
    log.info("Order Update");

    validateOrderItems(orderUpdateDTO.getOrderItems());

    Order order = orderRepository.findByIdAndUserId(orderId, SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> {
          log.warn("Order Update Failed - Existing Order with given id not found");
          return new OrderNotFound("Order not found");
        });
    log.debug("Order Update Info - Existing Order is fetched");

    order.getOrderItems().clear();
    orderRepository.saveAndFlush(order);

    Set<UUID> productIdsSet = orderUpdateDTO.getOrderItems().stream().map(OrderItemCreateDTO::getProductId).collect(Collectors.toSet());
    Map<UUID, ProductSummary> productSummaryMap = fetchBatchProduct(productIdsSet);
    Order newOrderInstance = createOrderInstance(orderUpdateDTO.toCreateDTO(), productSummaryMap);
    orderMapper.fromUpdateEntityToOrderEntity(newOrderInstance, order);

    orderRepository.save(order);
    log.info("Order Update Success");
  }

  public void updateOrderStatus(UUID orderId){
    log.info("Update Order Status");
    orderRepository.updateOrderStatus(OrderStatus.DELIVERED, orderId, SecurityUtils.getCurrentUserId());
  }

  private Map<UUID, ProductSummary> fetchBatchProduct(Set<UUID> productIdsSet){
    RestApiResponse<List<ProductSummary>> apiResponse = productServiceClient.getProductBatch(productIdsSet);

    if(Objects.isNull(apiResponse.getData()) || !apiResponse.getSuccess()){
      log.warn("Order Creation Failed - Product with given ids doesn't exists");
      throw new IllegalArgumentException("Product with given ids doesn't exists");
    }

    Map<UUID, ProductSummary> productMap =  apiResponse.getData().stream().collect(Collectors.toMap(ProductSummary::getId, Function.identity()));
    Set<UUID> missingIds = productIdsSet.stream()
        .filter(id -> !productMap.containsKey(id))
        .collect(Collectors.toSet());

    if (!missingIds.isEmpty()) {
      log.warn("Products not found: {}", missingIds);
      throw new IllegalArgumentException("Products not found ");  // may be some other custom exception
    }

    return productMap;
  }

  private BigDecimal calculateTotalPrice(@NonNull List<OrderItemCreateDTO> items, Map<UUID, ProductSummary> productMap) {
    return items.stream()
        .map(item -> productMap.get(item.getProductId())
            .getPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private @NonNull Order createOrderInstance(@NonNull OrderCreateDTO orderCreateDTO, Map<UUID, ProductSummary> productmap){
    Order orderInstance = orderMapper.toOrderEntity(orderCreateDTO);
    orderInstance.setUserId(SecurityUtils.getCurrentUserId());
    orderInstance.setEmail(jwtService.extractEmail(SecurityUtils.getAccessToken()));

    for(OrderItemCreateDTO orderItemCreateDTO : orderCreateDTO.getOrderItems()) {
      ProductSummary product = productmap.get(orderItemCreateDTO.getProductId());

      if (product == null) {
        log.warn("Product not found: {}", orderItemCreateDTO.getProductId());
        throw new IllegalArgumentException("Product not found");
      }
      orderInstance.addOrderItem(orderMapper.toOrderItemEntity(orderItemCreateDTO));
    }

    orderInstance.setOrderStatus(OrderStatus.PENDING);
    orderInstance.setTotalPrice(calculateTotalPrice(orderCreateDTO.getOrderItems(), productmap));
    return orderInstance;
  }

  @Transactional
  public void deleteOrderById(@NotNull UUID orderId) {
    log.info("Order Deletion");

    int deleted = orderRepository.deleteByIdAndUserId(orderId, SecurityUtils.getCurrentUserId());
    if (deleted == 0) {
      throw new OrderNotFound("Order doesn't exist");
    }
    log.info("Order Deletion Success");
  }

  public List<OrderItemResponseDTO> getOrderItemsOfOrder(UUID orderId){
    if(Objects.isNull(orderId)){
      log.warn("Fetching Order Items of Order Failed - Order Id is null");
      throw new IllegalArgumentException("Order Id is null");
    }

    return getOrder(orderId).getOrderItems();
  }

  public boolean existsById(UUID orderId){
    if(Objects.isNull(orderId)){
      log.warn("Order Id is null");
      throw new IllegalArgumentException("Order Id is null");
    }

    return orderRepository.existsByIdAndUserId(orderId, SecurityUtils.getCurrentUserId());
  }

  private void validateOrderItems(List<OrderItemCreateDTO> orderItems){
    if(Objects.isNull(orderItems) || orderItems.isEmpty()) {
      log.warn("Validate Order Items Failed - Order Items is empty");
      throw new EmptyProductsOrderCreationException("No products in order");
    }

    for(OrderItemCreateDTO orderItemCreateDTO : orderItems){
      if(orderItemCreateDTO.getQuantity() <= 0) {
        log.warn("Validate Order Items Failed - Order Item Quantity cannot be less than or equal to 0");
        throw new ZeroItemQuantityInOrderException("Order Item Quantity less than or equal to 0");
      }
    }
  }
}
