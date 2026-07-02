package com.Ecommerce.OrderService.Service;

import cn.hutool.core.lang.Snowflake;
import com.Ecommerce.OrderService.Config.StripeConfig;
import com.Ecommerce.OrderService.DTOs.Request.PaymentCreateDto;
import com.Ecommerce.OrderService.DTOs.Request.ProductRequest;
import com.Ecommerce.OrderService.DTOs.Response.OrderDetailsResponseDTO;
import com.Ecommerce.OrderService.DTOs.Response.StripeResponse;
import com.Ecommerce.OrderService.Exception.OrderNotFound;
import com.Ecommerce.OrderService.Mapper.PaymentMapper;
import com.Ecommerce.OrderService.Model.Payment;
import com.Ecommerce.OrderService.Model.PaymentStatus;
import com.Ecommerce.OrderService.Repository.PaymentRepository;
import com.Ecommerce.OrderService.Security.SecurityUtils;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Handles payment related services.
 *
 * @see PaymentMapper
 * @see StripeService
 * @see OrderService
 * @see PaymentRepository
 * @see StripeConfig
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
  private final PaymentMapper paymentMapper;
  private final StripeService stripeService;
  private final OrderService orderService;
  private final Snowflake snowflake;
  private final PaymentRepository paymentRepository;
  private final StripeConfig stripeConfig;

  @Transactional
  public StripeResponse initiateCheckout(UUID orderId, PaymentCreateDto paymentCreateDTO) {
    if (!orderService.existsById(orderId)) {
      log.warn("Order with given order Id doesn't exists");
      throw new OrderNotFound("Order not found");
    }

    OrderDetailsResponseDTO orderDetailsResponseDTO = orderService.getOrder(orderId);
    ProductRequest productRequest = ProductRequest.builder()
        .orderNumber(orderDetailsResponseDTO.getOrderNumber())
        .currency("USD")
        .orderItems(orderDetailsResponseDTO.getOrderItems())
        .build();

    StripeResponse response = stripeService.checkOut(productRequest);
    Payment newPayment = paymentMapper.toPaymentEntity(paymentCreateDTO);
    newPayment.setUserId(SecurityUtils.getCurrentUserId());
    newPayment.setPaymentStatus(PaymentStatus.PENDING);
    newPayment.setOrderId(orderId);
    newPayment.setTotalAmount(orderDetailsResponseDTO.getTotalPrice());
    newPayment.setPaymentNumber(snowflake.nextIdStr());
    newPayment.setStripeSessionId(response.getSessionId());

    paymentRepository.save(newPayment);
    return response;
  }

  @Transactional
  public void fullFillOrder(@NonNull Session session) {
    log.info("Fulfill Order");
    Payment payment = paymentRepository
        .findByStripeSessionId(session.getId())
        .orElseThrow(() -> new RuntimeException("Payment not found for session."));

    payment.setStripePaymentIntentId(session.getPaymentIntent());
    payment.setPaymentStatus(PaymentStatus.COMPLETED);

    paymentRepository.save(payment);
    orderService.confirmOrder(payment.getOrderId(), payment.getUserId());
  }

  @Transactional
  public void handleWebhook(String payload, String signHead) throws SignatureVerificationException {
    log.info("Handle Webhook was called");
    Event event = Webhook.constructEvent(payload, signHead, stripeConfig.getWebhookSecretKey());
    log.info("Event type: {}", event.getType());

    if (StripeConfig.checkOutSessionCompleted.equals(event.getType())) {
      log.info("Handle Webhook Info - Session Completed");
      Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

      if (StripeConfig.paidStatusFromSession.equals(session.getPaymentStatus())) {
        log.info("Handle Webhook Info - Payment Completed");
        fullFillOrder(session);
      } else {
        log.info("Session completed but payment status is: {}", session.getPaymentStatus());
      }
    } else {
      log.info("Ignored event type: {}", event.getType());
    }
  }

  public StripeResponse getSessionStatus(@NotNull String sessionId) throws StripeException {
    Session session = Session.retrieve(sessionId);
    String paymentStatus = session.getPaymentStatus();

    return StripeResponse.builder()
        .status(session.getStatus())
        .message(paymentStatus.equals("paid") ? "Payment Done" : "Payment Yet To Be Done")
        .sessionId(session.getId())
        .sessionUrl(session.getUrl())
        .build();
  }
}
