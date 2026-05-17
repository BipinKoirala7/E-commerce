package com.Ecommerce.ProductService.Filters;

import com.Ecommerce.ProductService.Exception.UnVerifiedSourceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;

@Component
@Slf4j
@Order(1)
public class SourceAuthenticationFilter extends OncePerRequestFilter {
  private static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";
  private static final String SERVICE_SECRET_HEADER = "X-Service-Secret";

  @Value("${app.gateway.secret}")
  private String GATEWAY_SECRET;

  @Value("${app.service.secret}")
  private String SERVICE_SECRET;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/actuator/health") ||
        path.startsWith("/actuator/info");
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    log.debug("Source Authentication Filter...");
    String path = request.getServletPath();

    boolean isInternalPath = path.contains("/internal");
    log.debug("Source Authentication Filter Info - Internal Route: {}", isInternalPath);

    String gatewaySecretHeader = request.getHeader(GATEWAY_SECRET_HEADER);
    log.debug("Source Authentication Filter Info - Gateway Secret Header Retrieved");

    String serviceSecretHeader = request.getHeader(SERVICE_SECRET_HEADER);
    log.debug("Source Authentication Filter Info - Service Secret Header Retrieved");

    if (isInternalPath) {
      log.debug("Source Authentication Filter Info - Internal Controller Request.");

      if (!isValidSecret(serviceSecretHeader, SERVICE_SECRET)) {
        log.debug("Source Authentication Filter Info - Service secret header is missing or incorrect");
        throw new UnVerifiedSourceException("Incorrect or Invalid Service Secret");
      }
    } else {
      log.debug("Source Authentication Filter Info - Gateway Request");

      if (!isValidSecret(gatewaySecretHeader, GATEWAY_SECRET)) {
        log.debug("Source Authentication Filter Info - Gateway secret header is missing or incorrect");
        throw new UnVerifiedSourceException("Incorrect or Invalid Gateway Secret");
      }
    }
    filterChain.doFilter(request, response);
  }

  /**
   * Validates the secret using constant-time comparison to prevent timing attacks
   */
  private boolean isValidSecret(String headerValue, String expectedSecret) {
    if (headerValue == null || headerValue.isBlank()) {
      return false;
    }
    return MessageDigest.isEqual(headerValue.getBytes(), expectedSecret.getBytes());
  }
}
