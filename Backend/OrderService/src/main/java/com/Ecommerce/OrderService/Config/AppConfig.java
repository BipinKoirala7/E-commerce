package com.Ecommerce.OrderService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class AppConfig {

  @Bean
  public AntPathMatcher antPathMatcher() {
    return new AntPathMatcher();
  }

}
