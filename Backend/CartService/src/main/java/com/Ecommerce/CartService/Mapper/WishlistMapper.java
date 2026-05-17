package com.Ecommerce.CartService.Mapper;

import com.Ecommerce.CartService.DTOs.Response.WishlistResponseDTO;
import com.Ecommerce.CartService.Model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WishlistMapper {

  WishlistResponseDTO toResponseDTO(Wishlist wishlist);
  Wishlist toWishlistEntity(UUID productId);
}
