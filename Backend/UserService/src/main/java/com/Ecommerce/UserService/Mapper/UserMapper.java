package com.Ecommerce.UserService.Mapper;

import com.Ecommerce.UserService.DTOs.Request.OAuthUserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserUpdateDTO;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  UserResponseDTO toResponseDTO(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "emailVerified", constant = "false")
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "providerId", ignore = true)
  @Mapping(target = "authProvider", constant = "LOCAL")
  @Mapping(target = "role", constant = "USER")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  User fromCreateDto(UserCreateDTO userCreateDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "emailVerified", constant = "false")
  @Mapping(target = "lastLoginAt", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "authProvider", constant = "GOOGLE")
  @Mapping(target = "role", constant = "USER")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  User fromOAuthCreateDto(OAuthUserCreateDTO oAuthUserCreateDTO);

  void fromUpdateDTOtoEntity(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

}
