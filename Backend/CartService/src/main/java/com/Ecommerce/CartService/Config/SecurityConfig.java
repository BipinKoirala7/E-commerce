package com.Ecommerce.CartService.Config;

import com.Ecommerce.CartService.Filters.FilterExceptionHandler;
import com.Ecommerce.CartService.Filters.JwtFilter;
import com.Ecommerce.CartService.Filters.SourceAuthenticationFilter;
import com.Ecommerce.CartService.Security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  private final JwtFilter jwtFilter;
  private final FilterExceptionHandler filterExceptionHandler;
  private final CorsConfig corsConfig;
  private final SourceAuthenticationFilter sourceAuthenticationFilter;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain httpSecurityFilterChain(@NonNull HttpSecurity http) {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfiguration()))
        .authorizeHttpRequests(request -> request.anyRequest().authenticated())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(filterExceptionHandler, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(sourceAuthenticationFilter, FilterExceptionHandler.class)
        .addFilterAfter(jwtFilter, SourceAuthenticationFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOriginPatterns(Arrays.asList(corsConfig.getAllowedOriginPatterns()));
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
