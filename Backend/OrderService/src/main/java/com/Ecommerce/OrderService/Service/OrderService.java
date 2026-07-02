package com.Ecommerce.OrderService.Service;

import cn.hutool.core.lang.Snowflake;
import com.Ecommerce.OrderService.Client.ProductServiceClient;
import com.Ecommerce.OrderService.DTOs.Request.OrderCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderItemCreateDto;
import com.Ecommerce.OrderService.DTOs.Request.OrderUpdateDto;
import com.Ecommerce.OrderService.DTOs.Response.*;
import com.Ecommerce.OrderService.Exception.*;
import com.Ecommerce.OrderService.Mapper.OrderMapper;
import com.Ecommerce.OrderService.Mapper.PaymentMapper;
import com.Ecommerce.OrderService.Model.Order;
import com.Ecommerce.OrderService.Model.OrderItem;
import com.Ecommerce.OrderService.Model.OrderStatus;
import com.Ecommerce.OrderService.Model.Payment;
import com.Ecommerce.OrderService.Repository.OrderRepository;
import com.Ecommerce.OrderService.Repository.PaymentRepository;
import com.Ecommerce.OrderService.Security.SecurityUtils;
import jakarta.validation.Valid;
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
  private final PaymentRepository paymentRepository;
  private final Snowflake snowflake;
  private final PaymentMapper paymentMapper;
  private final OrderMapper orderMapper;
  private final JwtService jwtService;

  @Transactional
  public void createNewOrder(@NonNull OrderCreateDTO orderCreateDTO) {
    log.info("Order Creation");

    validateOrderItems(orderCreateDTO.getOrderItems());

    Set<UUID> productIdsSet = orderCreateDTO.getOrderItems().stream().map(OrderItemCreateDto::getProductId).collect(Collectors.toSet());
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

  private Order findOrderByIdAndUser(UUID orderId) {
    return orderRepository.findByIdAndUserId(orderId, SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> new OrderNotFound("Order not found"));
  }

  public List<OrderItemResponseDTO> getOrderItemsOfOrder(UUID orderId) {
    if (Objects.isNull(orderId)) throw new IllegalArgumentException("Order Id is null");

    Order order = findOrderByIdAndUser(orderId);
    Set<UUID> productIds = order.getOrderItems().stream()
        .map(OrderItem::getProductId)
        .collect(Collectors.toSet());

    Map<UUID, ProductSummary> productSummaryMap = fetchBatchProduct(productIds);

    return order.getOrderItems().stream()
        .map(item -> new OrderItemResponseDTO(
            item.getId(),
            productSummaryMap.get(item.getProductId()),
            item.getQuantity()
        ))
        .toList();
  }

  public OrderDetailsResponseDTO getOrder(UUID orderId) {
    log.info("Fetching Order Details");
    Order order = findOrderByIdAndUser(orderId);
    OrderDetailsResponseDTO responseDTO = orderMapper.toDetailsResponseDTO(order);
    responseDTO.setOrderItems(new ArrayList<>());

    Set<UUID> productIds = order.getOrderItems().stream()
        .map(OrderItem::getProductId)
        .collect(Collectors.toSet());

    Map<UUID, ProductSummary> productSummaryMap = fetchBatchProduct(productIds);

    for (OrderItem item : order.getOrderItems()) {
      responseDTO.getOrderItems().add(new OrderItemResponseDTO(
          item.getId(),
          productSummaryMap.get(item.getProductId()),
          item.getQuantity()
      ));
    }
    responseDTO.setPayment(getPaymentByOrderId(order.getId()));
    return responseDTO;
  }

  public OrderDetailsResponseDTO getOrder(String orderNumber) {
    log.info("Fetching Order");

    Order order = orderRepository.findByOrderNumberAndUserId(orderNumber, SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> {
          log.warn("Fetching Order Failed - Order not found");
          return new OrderNotFound("Order with given order number not found");
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

    responseDTO.setPayment(getPaymentByOrderId(order.getId()));
    log.info("Fetching Order Success");
    return responseDTO;
  }

  @Transactional
  public void updateOrder(@NotNull UUID orderId, @NonNull OrderUpdateDto orderUpdateDTO) {
    log.info("Order Update");

    if(orderUpdateDTO.getOrderStatus() == null){
      log.warn("Order Update Failed - Order Status is null");
      throw new InValidOrderUpdate("Order Status is null");
    }

    if(orderUpdateDTO.getOrderStatus() == OrderStatus.CONFIRMED){
      log.warn("Order Update Failed - Order Status cannot be CONFIRMED");
      throw new InValidOrderUpdate("No Authority to perform this operation");
    }

    Order order = getOrderById(orderId);

    switch (order.getOrderStatus()) {
      case PENDING -> {
        if(orderUpdateDTO.getOrderStatus().equals(OrderStatus.CANCELLED)){
          order.setOrderStatus(OrderStatus.CANCELLED);
        } else {
          throw new InValidOrderUpdate("Order is not confirmed yet");
        }
      }
      case PROCESSING -> {
        if(orderUpdateDTO.getOrderStatus().equals(OrderStatus.CANCELLED)){
          order.setOrderStatus(OrderStatus.CANCELLED);
        } else if(orderUpdateDTO.getOrderStatus().equals(OrderStatus.DELIVERED)) {
          order.setOrderStatus(OrderStatus.DELIVERED);
        } else {
          throw new InValidOrderUpdate("Order is not delivered");
        }
      }
      case DELIVERED -> {
        if(orderUpdateDTO.getOrderStatus().equals(OrderStatus.CANCELLED)) {
          throw new InValidOrderUpdate("You can't cancel order at this stage");
        }
      }
      case CANCELLED -> throw new  InValidOrderUpdate("Order cannot be updated as it's cancelled");
      default -> throw new  InValidOrderUpdate("Order status is unknown");

    }

    orderRepository.save(order);
    log.info("Order Update Success");
  }

  public void confirmOrder(@NotNull UUID orderId, @NonNull UUID userId) {
    log.info("Confirming Order");

    Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> new  OrderNotFound("Order with given id not found"));
    if(order.getOrderStatus().equals(OrderStatus.PENDING)) {
      order.setOrderStatus(OrderStatus.CONFIRMED);
      orderRepository.save(order);
    } else  {
      throw new InValidOrderUpdate("Order is not confirmed yet");
    }
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

  private BigDecimal calculateTotalPrice(@NonNull List<OrderItemCreateDto> items, Map<UUID, ProductSummary> productMap) {
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

    for(OrderItemCreateDto orderItemCreateDTO : orderCreateDTO.getOrderItems()) {
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

//    Order order = orderRepository.findByIdAndUserId(orderId, SecurityUtils.getCurrentUserId())
//        .orElseThrow(() -> new OrderNotFound("Order with given id not found"));
//
//    if(order.getOrderStatus().equals(OrderStatus.PENDING)) {
//
//    }

    int deleted = orderRepository.deleteByIdAndUserId(orderId, SecurityUtils.getCurrentUserId());
    if (deleted == 0) {
      throw new OrderNotFound("Order doesn't exist");
    }
    log.info("Order Deletion Success");
  }

  private Order getOrderById(@NotNull UUID orderId) {
    log.info("Getting Order");
    return orderRepository
        .findByIdAndUserId(orderId, SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> new  OrderNotFound("Order with given id not found"));
  }

  public PaymentResponseDto getPaymentByOrderId(@Valid UUID orderId) {
    log.info("Fetching Payment by Order Id");

    return paymentRepository.findByOrderId(orderId)
        .map(paymentMapper::toPaymentResponseDto)
        .orElseGet(() -> {
          log.warn("Payment not found for Order Id: {}", orderId);
          return null;
        });
  }

  public boolean existsById(UUID orderId){
    if(Objects.isNull(orderId)){
      log.warn("Order Id is null");
      throw new IllegalArgumentException("Order Id is null");
    }

    return orderRepository.existsByIdAndUserId(orderId, SecurityUtils.getCurrentUserId());
  }

  private void validateOrderItems(List<OrderItemCreateDto> orderItems){
    if(Objects.isNull(orderItems) || orderItems.isEmpty()) {
      log.warn("Validate Order Items Failed - Order Items is empty");
      throw new EmptyProductsOrderCreationException("No products in order");
    }

    for(OrderItemCreateDto orderItemCreateDTO : orderItems){
      if(orderItemCreateDTO.getQuantity() <= 0) {
        log.warn("Validate Order Items Failed - Order Item Quantity cannot be less than or equal to 0");
        throw new ZeroItemQuantityInOrderException("Order Item Quantity less than or equal to 0");
      }
    }
  }
}
