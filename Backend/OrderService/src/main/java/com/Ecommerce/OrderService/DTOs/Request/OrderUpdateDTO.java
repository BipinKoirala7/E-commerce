package com.Ecommerce.OrderService.DTOs.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateDTO {

  @NotNull
  private String billingAddress;

  @NotNull
  private String shippingAddress;

  @NotNull
  private String phone;

  @NotEmpty
  private List<OrderItemCreateDTO> orderItems;

  public OrderCreateDTO toCreateDTO(){
    OrderCreateDTO createDTO = new OrderCreateDTO();
    createDTO.setBillingAddress(this.billingAddress);
    createDTO.setShippingAddress(this.shippingAddress);
    createDTO.setPhone(this.phone);
    createDTO.setOrderItems(this.orderItems);
    return createDTO;
  }
}
