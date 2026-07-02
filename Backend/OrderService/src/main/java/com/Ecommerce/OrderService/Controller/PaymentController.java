package com.Ecommerce.OrderService.Controller;

import com.Ecommerce.OrderService.DTOs.Request.PaymentCreateDto;
import com.Ecommerce.OrderService.DTOs.Response.RestApiResponse;
import com.Ecommerce.OrderService.DTOs.Response.StripeResponse;
import com.Ecommerce.OrderService.Service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("{orderId}/pay")
  public ResponseEntity<RestApiResponse<StripeResponse>> payForOrder(@PathVariable @Valid UUID orderId, @RequestBody @Valid PaymentCreateDto paymentCreateDTO) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK.value(), paymentService.initiateCheckout(orderId, paymentCreateDTO), "Successfully Paid for Order"));
  }

  @PostMapping("webhook")
  public ResponseEntity<RestApiResponse<Void>> webhookGetMapper(HttpServletRequest request, @RequestHeader("Stripe-Signature") String signHeader) throws SignatureVerificationException, IOException {
    byte[] payloadBytes = request.getInputStream().readAllBytes();
    String payload = new String(payloadBytes, StandardCharsets.UTF_8);

    log.info("Payload bytes length: {}", payloadBytes.length);
    log.info("First 100 chars: {}", new String(payloadBytes, StandardCharsets.UTF_8).substring(0, Math.min(100, payloadBytes.length)));

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
