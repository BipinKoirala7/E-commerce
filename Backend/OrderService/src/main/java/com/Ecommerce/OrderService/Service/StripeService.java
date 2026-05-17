package com.Ecommerce.OrderService.Service;

import com.Ecommerce.OrderService.DTOs.Request.ProductRequest;
import com.Ecommerce.OrderService.DTOs.Response.StripeResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles checkout with Stripe Checkout.
 * */
@Service
@Slf4j
public class StripeService {

  public StripeResponse checkOut(@NonNull ProductRequest productRequest) {
    List<SessionCreateParams.LineItem> lineItems = productRequest.getOrderItems().stream().map(item -> {
      SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
          .setName(item.getProductSummary().getName()).build();

      SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
          .setCurrency(productRequest.getCurrency())
          .setUnitAmount(item.getProductSummary().getPrice().longValue())
          .setProductData(productData)
          .build();

      return SessionCreateParams.LineItem.builder()
          .setPriceData(priceData)
          .setQuantity(item.getQuantity().longValue())
          .build();
    }).toList();

    SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .addAllLineItem(lineItems)
        .setSuccessUrl("http://localhost:4000/order/" + productRequest.getOrderId() + "/checkout/success")
        .setCancelUrl("http://localhost:8080/order/" + productRequest.getOrderId() + "/checkout/error")
        .build();

    Session session;
    try {
      session = Session.create(sessionCreateParams);
    } catch (StripeException e) {
      log.error("Stripe error: {}", e.getMessage(), e);
      return StripeResponse.builder()
          .status("FAILED")
          .message("Error occurred")
          .build();
    }

    return StripeResponse.builder()
        .status(session.getStatus())
        .message("SUCCESS")
        .sessionId(session.getId())
        .sessionUrl(session.getUrl())
        .build();
  }
}
