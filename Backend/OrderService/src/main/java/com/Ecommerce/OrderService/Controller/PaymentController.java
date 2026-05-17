package com.Ecommerce.OrderService.Controller;

import com.Ecommerce.OrderService.DTOs.Request.PaymentCreateDTO;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import com.Ecommerce.OrderService.DTOs.Response.StripeResponse;
import com.Ecommerce.OrderService.Service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("{orderId}/pay")
  public ResponseEntity<RestApiResponse<StripeResponse>> payForOrder(@PathVariable @Valid UUID orderId, @RequestBody @Valid PaymentCreateDTO paymentCreateDTO){
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), paymentService.initiateCheckout(orderId, paymentCreateDTO), "Successfully Paid for Order"));
  }

  @PostMapping("webhook")
  public ResponseEntity<RestApiResponse<Void>> webhookGetMapper(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signHeader) throws SignatureVerificationException {
    paymentService.handleWebhook(payload, signHeader);
    return ResponseEntity
        .status(HttpStatus.ACCEPTED)
        .body(RestApiResponse.success(HttpStatus.ACCEPTED.value(), "Payment Status Updated"));
  }

  @GetMapping("session/{sessionId}/status")
  public ResponseEntity<RestApiResponse<StripeResponse>> getSessionStatus(@PathVariable String sessionId) throws StripeException {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), paymentService.getSessionStatus(sessionId), "Session Status Retrieved"));
  }
}
