package com.Ecommerce.OrderService.Mapper;

import com.Ecommerce.OrderService.DTOs.Request.OrderCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderItemCreateDTO;
import com.Ecommerce.OrderService.DTOs.Request.OrderUpdateDTO;
import com.Ecommerce.OrderService.DTOs.Response.OrderDetailsResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.OrderListResponseDTO;
import com.Ecommerce.OrderService.Model.Order;
import com.Ecommerce.OrderService.Model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

  @Mapping(target = "orderItems", ignore = true)
  OrderDetailsResponseDTO toDetailsResponseDTO(Order order);

  @Mapping(target = "noOfItems", ignore = true)
  OrderListResponseDTO toListResponseDTO(Order order);

  @Mapping(target = "orderItems", ignore = true)
//  @Mapping(target = "orderStatus", constant = "PENDING")
  Order toOrderEntity(OrderCreateDTO orderCreateDTO);
  OrderItem toOrderItemEntity(OrderItemCreateDTO orderItemCreateDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userId", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "orderItems", ignore = true)
  @Mapping(target = "orderStatus", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void fromUpdateEntityToOrderEntity(Order updatedOrder, @MappingTarget Order order);
}
