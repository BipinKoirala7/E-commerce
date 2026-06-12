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
  Order toOrderEntity(OrderCreateDTO orderCreateDTO);
  OrderItem toOrderItemEntity(OrderItemCreateDTO orderItemCreateDTO);

}
