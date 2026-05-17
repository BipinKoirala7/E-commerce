package com.Ecommerce.APIGateway.Config;

import com.Ecommerce.APIGateway.Security.SecurityUtils;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Value("${app.service.secret}")
  private String serviceSecret;

  private final String AUTH_HEADER_NAME = "Authorization";
  private final String AUTH_HEADER_PREFIX = "Bearer ";
  private final String SERVICE_HEADER_NAME = "X-Service-Secret";

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate ->
        requestTemplate
            .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + SecurityUtils.getAccessToken())
            .header(SERVICE_HEADER_NAME, serviceSecret);
  }
}