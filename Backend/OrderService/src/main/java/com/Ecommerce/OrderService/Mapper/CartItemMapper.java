package com.Ecommerce.OrderService.Mapper;

import com.Ecommerce.OrderService.DTOs.Request.CartItemUpdateDTO;
import com.Ecommerce.OrderService.DTOs.Response.CartItemResponseDTO;
import com.Ecommerce.OrderService.Model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartItemMapper {

  CartItemResponseDTO toResponseDTO(CartItem cartItem);

  @Mapping(target = "quantity", constant = "1")
  CartItem toCartItemEntity(UUID productId);

  void fromUpdateDtoToCartItemEntity(CartItemUpdateDTO cartItemUpdateDTO, @MappingTarget CartItem cartItem);
}

