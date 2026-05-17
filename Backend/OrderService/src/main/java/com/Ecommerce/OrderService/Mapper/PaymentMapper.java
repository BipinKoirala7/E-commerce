package com.Ecommerce.OrderService.Mapper;

import com.Ecommerce.OrderService.DTOs.Request.PaymentCreateDTO;
import com.Ecommerce.OrderService.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

  Payment toPaymentEntity(PaymentCreateDTO paymentCreateDTO);
}
