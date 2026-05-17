package com.Ecommerce.UserService.Config;

import com.Ecommerce.UserService.Filters.FilterExceptionHandler;
import com.Ecommerce.UserService.Filters.JwtFilter;
import com.Ecommerce.UserService.Filters.SourceAuthenticationFilter;
import com.Ecommerce.UserService.Security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

/**
 * Central Spring Security configuration for the UserService.
 *
 * <p>Defines the {@link SecurityFilterChain} that governs HTTP security across all
 * endpoints, including authentication, authorization, CORS, and session management.
 *
 * <h2>Filter Chain Order</h2>
 * Filters are applied in the following sequence:
 * <ol>
 *   <li>{@link FilterExceptionHandler} – catches exceptions thrown by downstream filters
 *       and converts them into proper HTTP error responses before they bypass
 *       {@code @ControllerAdvice}.</li>
 *   <li>{@link SourceAuthenticationFilter} – validates the origin/source of the request
 *       (e.g. internal vs. external callers).</li>
 *   <li>{@link JwtFilter} – extracts and validates the JWT token, then populates the
 *       {@link org.springframework.security.core.context.SecurityContext}.</li>
 * </ol>
 *
 * <h2>Public Endpoints</h2>
 * The following paths are whitelisted and require no authentication:
 * <ul>
 *   <li>{@code /auth/register}, {@code /auth/login}</li>
 *   <li>{@code /auth/oauth/**}, {@code /oauth2/authorization/**}</li>
 *   <li>{@code /login/oauth2/code/**} – OAuth2 redirect callback</li>
 *   <li>{@code /internal/auth/token-refresh} – internal service token renewal</li>
 * </ul>
 *
 * <h2>Session Policy</h2>
 * Sessions are created only when required ({@code IF_REQUIRED}), primarily to support
 * the OAuth2 authorization code flow, which needs a session to preserve state between
 * the redirect and the callback.
 *
 * @see JwtFilter
 * @see SourceAuthenticationFilter
 * @see FilterExceptionHandler
 * @see CustomAuthenticationEntryPoint
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  private final JwtFilter jwtFilter;
  private final FilterExceptionHandler filterExceptionHandler;
  private final SourceAuthenticationFilter sourceAuthenticationFilter;
  private final CorsConfig corsConfig;
  private final OAuthSuccessHandler oAuthSuccessHandler;
  private final OAuthFailureHandler oAuthFailureHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain httpSecurityFilterChain(@NonNull HttpSecurity http) {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfiguration()))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(request -> request
            .requestMatchers(
                "/auth/register",
                "/auth/login",
                "/auth/oauth/**",
                "/oauth2/authorization/**",
                "/login/oauth2/code/**",
                "/internal/auth/token-refresh"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth ->
            oauth
                //  .authorizationEndpoint(auth -> auth.baseUri("/auth/oauth2"))
                //  .redirectionEndpoint(endpoint -> endpoint.baseUri("/api/login/oauth2/code/*"))
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailureHandler))
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
