package com.Ecommerce.OrderService.Controller;

import com.Ecommerce.OrderService.DTOs.Request.OrderCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderUpdateDTO;
import com.Ecommerce.OrderService.DTOs.Response.OrderDetailsResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.OrderListResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import com.Ecommerce.OrderService.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<RestApiResponse<Void>> createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
    this.orderService.createNewOrder(orderCreateDTO);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(RestApiResponse.success(HttpStatus.CREATED.value(), "Order Created Successfully"));
  }

  @GetMapping
  public ResponseEntity<RestApiResponse<List<OrderListResponseDTO>>> getAllOrders() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), this.orderService.getAllOrders(), "Successfully Fetched Orders"));
  }

  @GetMapping("{orderId}")
  public ResponseEntity<RestApiResponse<OrderDetailsResponseDTO>> getOrderInfo(@PathVariable UUID orderId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), this.orderService.getOrder(orderId), "Successfully Fetched Order"));
  }

  @PutMapping("{orderId}")
  public ResponseEntity<RestApiResponse<Void>> updateOrder(@Valid @RequestBody OrderUpdateDTO orderUpdateDTO, @PathVariable UUID orderId) {
    this.orderService.updateOrder(orderId, orderUpdateDTO);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), "Order Updated Successfully"));
  }

  @DeleteMapping("{orderId}")
  public ResponseEntity<RestApiResponse<Void>> deleteOrder(@PathVariable UUID orderId) {
    this.orderService.deleteOrderById(orderId);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(RestApiResponse.success(HttpStatus.NOT_FOUND.value(), "Order Deleted Successfully"));
  }
}
