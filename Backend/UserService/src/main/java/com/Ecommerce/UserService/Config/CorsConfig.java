package com.Ecommerce.UserService.Config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
* Configuration Properties for Cors Config. Values would come from app.cors in application.yaml
* */
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
