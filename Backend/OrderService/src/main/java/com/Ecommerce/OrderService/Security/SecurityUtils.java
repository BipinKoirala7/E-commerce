package com.Ecommerce.OrderService.Security;

import com.Ecommerce.OrderService.Exception.TokenAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
public class SecurityUtils {
  public static UUID getCurrentUserId() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken");
      throw new TokenAuthenticationException("Authentication is required to access this resource");
    }
    log.debug("No Authentication found");

    return ((JwtAuthenticationToken) authentication).getUserId();
  }

  public static String getAccessToken() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken.");
      throw new TokenAuthenticationException("Authentication is required to access this resource");
    }
    log.debug("No Authentication found.");

    return ((JwtAuthenticationToken) authentication).getAccessToken();
  }
}