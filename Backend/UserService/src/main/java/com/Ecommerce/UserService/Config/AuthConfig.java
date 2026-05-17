package com.Ecommerce.UserService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;

/*
* Security Beans for path matching and password Encoding
* */
@Configuration
public class AuthConfig {

  @Bean
  public AntPathMatcher antPathMatcher() {
    return new AntPathMatcher();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
