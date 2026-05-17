package com.Ecommerce.CartService.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cors")
@Data
public class CorsConfig {
  private Boolean enabled;
  private String[] allowedOriginPatterns;
  private String[] allowedMethods;
  private String[] allowedHeaders;
  private String[] exposedHeaders;
  private Boolean allowCredentials;
  private Long maxAge;
}
