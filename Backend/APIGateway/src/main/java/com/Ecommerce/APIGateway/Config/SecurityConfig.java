package com.Ecommerce.APIGateway.Config;

import com.Ecommerce.APIGateway.Filters.FilterExceptionHandler;
import com.Ecommerce.APIGateway.Filters.JwtFilter;
import com.Ecommerce.APIGateway.Security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
  private final FilterExceptionHandler filterExceptionHandler;
  private final CorsConfig corsConfig;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain httpSecurityFilterChain(@NonNull HttpSecurity http) {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfiguration()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/login/oauth2/code/**").permitAll()
            .requestMatchers("/oauth2/authorization/**").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/product/**").permitAll()
            .requestMatchers("/payment/webhook").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(filterExceptionHandler, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtFilter, FilterExceptionHandler.class)
        .build();
  }

  private @NonNull CorsConfigurationSource corsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
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
