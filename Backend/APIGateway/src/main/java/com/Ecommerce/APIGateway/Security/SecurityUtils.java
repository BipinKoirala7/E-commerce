package com.Ecommerce.APIGateway.Security;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
public class SecurityUtils {
  public static @Nullable UUID getCurrentUserId() {
    log.debug("Getting User ID from Security Context");
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken");
      //  throw new TokenAuthenticationException("Authentication is required to access this resource");
      return null;
    }
    log.debug("Authentication is of type JwtAuthenticationToken");

    return ((JwtAuthenticationToken) authentication).getUserId();
  }

  public static @Nullable String getAccessToken() {
    log.debug("Getting Access Token from Security Context");
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken)) {
      log.warn("Authentication is not of type JwtAuthenticationToken.");
      //  throw new TokenAuthenticationException("Authentication is required to access this resource");
      return null;
    }
    log.debug("Authentication is of type JwtAuthenticationToken.");

    return ((JwtAuthenticationToken) authentication).getAccessToken();
  }
}