package com.Ecommerce.OrderService.Config;

import com.Ecommerce.OrderService.Filters.FilterExceptionHandler;
import com.Ecommerce.OrderService.Filters.JwtFilter;
import com.Ecommerce.OrderService.Filters.SourceAuthenticationFilter;
import com.Ecommerce.OrderService.Security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  private final JwtFilter jwtFilter;
  private final CorsConfig corsConfig;
  private final FilterExceptionHandler filterExceptionHandler;
  private final SourceAuthenticationFilter sourceAuthenticationFilter;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  @Order(1)
  public SecurityFilterChain actuatorFilterChain(HttpSecurity http) {
    http
        .securityMatcher("/actuator/**")
        .cors(cors -> cors.configurationSource(corsConfiguration()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain httpSecurityFilterChain(@NonNull HttpSecurity http) {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/payment/webhook").permitAll()
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(filterExceptionHandler, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(sourceAuthenticationFilter, FilterExceptionHandler.class)
        .addFilterAfter(jwtFilter, SourceAuthenticationFilter.class)
        .build();
  }

  private @NonNull CorsConfigurationSource corsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.applyPermitDefaultValues();
    corsConfiguration.setAllowedOrigins(Arrays.asList(corsConfig.getAllowedOrigins()));
    corsConfiguration.setAllowedHeaders(Arrays.asList(corsConfig.getAllowedHeaders()));
    corsConfiguration.setAllowedMethods(Arrays.asList(corsConfig.getAllowedMethods()));
    corsConfiguration.setAllowCredentials(corsConfig.getAllowCredentials());
    corsConfiguration.setExposedHeaders(Arrays.asList(corsConfig.getExposedHeaders()));
    corsConfiguration.setMaxAge(corsConfig.getMaxAge());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
