package com.Ecommerce.CartService.Filters;

import com.Ecommerce.CartService.Exception.InValidTokenException;
import com.Ecommerce.CartService.Security.JwtAuthenticationToken;
import com.Ecommerce.CartService.Service.JwtService;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final String AUTH_HEADER = "Authorization";
  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    log.debug("Jwt Authentication Filter...");

    String token = jwtService.extractBearerToken(request.getHeader(AUTH_HEADER));
    log.debug("Jwt Authentication Info - Extracted Bearer Token");

    if (!jwtService.validateAccessToken(token)) {
      log.debug("Jwt Authentication Failed - Access Token is invalid");
      throw new InValidTokenException("Token is invalid");
    }
    log.debug("Jwt Authentication Info - Access Token is valid");

    UUID userId = UUID.fromString(jwtService.extractSubject(token));
    log.debug("Jwt Authentication Info - Extracted User Id from Access Token");

    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token, userId, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    log.debug("Security Context established");

    filterChain.doFilter(request, response);
  }
}
