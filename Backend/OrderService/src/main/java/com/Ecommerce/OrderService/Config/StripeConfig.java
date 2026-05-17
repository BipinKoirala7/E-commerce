package com.Ecommerce.OrderService.Config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StripeConfig {

  @Value("${stripe.secret-key}")
  private String secretKey;

  @Value("${stripe.webhook-secret}")
  private String webhookSecretKey;

  public static String checkOutSessionCompleted = "checkout.session.completed";
  public static String paidStatusFromSession = "paid";

  @PostConstruct
  public void init(){
    Stripe.apiKey = this.secretKey;
  }
}
