package com.Ecommerce.OrderService.Mapper;

import com.Ecommerce.OrderService.DTOs.Request.PaymentCreateDto;
import com.Ecommerce.OrderService.DTOs.Response.PaymentResponseDto;
import com.Ecommerce.OrderService.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

  PaymentResponseDto toPaymentResponseDto(Payment payment);
  Payment toPaymentEntity(PaymentCreateDto paymentCreateDTO);
}
