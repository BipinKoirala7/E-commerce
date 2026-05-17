package com.Ecommerce.APIGateway.Filters;

import com.Ecommerce.APIGateway.DTOs.TokenDTO;
import com.Ecommerce.APIGateway.Exception.EmptyTokenException;
import com.Ecommerce.APIGateway.Security.JwtAuthenticationToken;
import com.Ecommerce.APIGateway.Service.CookieService;
import com.Ecommerce.APIGateway.Service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final AntPathMatcher pathMatcher;
  private final CookieService cookieService;
  private static final List<String> EXCLUDED_PATHS = Arrays.asList(
      "/auth/register",
      "/auth/login",
      "/login/oauth2/code/**",
      "/oauth2/authorization/**",
      "/product/**",
      "/payment/webhook"
  );

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String path = request.getServletPath();
    boolean shouldSkip = EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));

    if (shouldSkip) {
      log.debug("Jwt Filter Skipped - Path: {}", path);
    }

    return shouldSkip;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String accessTokenForContext = null;
    try {
      log.debug("Jwt Authentication Filter...");

      // Validate the token from the request cookie
      String accessToken = cookieService.extractAccessTokenFromRequest(request);
      log.debug("Jwt Authentication Info - Access Token found in cookie");

      jwtService.validateAccessToken(accessToken);
      log.debug("Jwt Filter Authentication Info - Access Token is valid");

      // Rotate the token with new ones
      TokenDTO newTokenSet = jwtService.refreshToken(request, response);
      accessTokenForContext = newTokenSet.getAccessToken();
      log.debug("Jwt Filter Authentication Info - Token refreshed successfully, new Access Token set in cookie");

    } catch (ExpiredJwtException | EmptyTokenException e) {
      // If the access token is expired or missing, attempt to refresh the token using the refresh token
      log.debug("Jwt Filter Authentication Info - Access Token is expired or missing, attempting to refresh token");
      TokenDTO tokenDTO = jwtService.refreshToken(request, response);
      accessTokenForContext = tokenDTO.getAccessToken();

    }
      createSecurityContext(accessTokenForContext);
      filterChain.doFilter(request, response);
  }

  private void createSecurityContext(String accessToken) {
    UUID userId = UUID.fromString(jwtService.extractSubject(accessToken));
    log.debug("Jwt Filter Authentication Info - User ID extracted from Access Token");

    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
        accessToken,
        userId,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    log.debug("Jwt Filter Authentication Info - Security context established");
  }
}
