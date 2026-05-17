package com.Ecommerce.CartService.Security;

import com.Ecommerce.CartService.Exception.TokenAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
public class SecurityUtils {
  public static UUID getCurrentUserId() {
    log.debug("Getting User ID from Security Context");
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken");
      throw new TokenAuthenticationException("Authentication is required to access this resource");
    }
    log.debug("Authentication is of type JwtAuthenticationToken");

    return ((JwtAuthenticationToken) authentication).getUserId();
  }

  public static String getAccessToken() {
    log.debug("Getting Access Token from Security Context");
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken.");
      throw new TokenAuthenticationException("Authentication is required to access this resource");
    }
    log.debug("Authentication is of type JwtAuthenticationToken.");

    return ((JwtAuthenticationToken) authentication).getAccessToken();
  }
}