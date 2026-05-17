package com.Ecommerce.APIGateway.Config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cors")
@Data
@Getter
public class CorsConfig {
  private Boolean enabled;
  private String[] allowedOrigins;
  private String[] allowedMethods;
  private String[] allowedHeaders;
  private String[] exposedHeaders;
  private Boolean allowCredentials;
  private Long maxAge;
}
