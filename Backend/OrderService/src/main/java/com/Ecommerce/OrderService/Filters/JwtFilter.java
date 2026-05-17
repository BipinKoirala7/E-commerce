package com.Ecommerce.OrderService.Filters;

import com.Ecommerce.OrderService.Security.JwtAuthenticationToken;
import com.Ecommerce.OrderService.Service.JwtService;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final String AUTH_HEADER = "Authorization";
  private final JwtService jwtService;
  private final AntPathMatcher pathMatcher;

  private static final List<String> EXCLUDED_PATHS = List.of(
      "/payment/webhook"
  );

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request){
    String path = request.getServletPath();
    boolean shouldSkip = EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));

    if (shouldSkip) {
      log.debug("Jwt Filter Skipped - Path: {}", path);
    }

    return shouldSkip;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    log.debug("Jwt Authentication Filter...");

    String token = jwtService.extractBearerToken(request.getHeader(AUTH_HEADER));
    log.debug("Jwt Authentication Info - Extracted Bearer Token");

    jwtService.validateAccessToken(token);
    log.debug("Jwt Authentication Info - Access Token is valid");

    UUID userId = UUID.fromString(jwtService.extractSubject(token));
    log.debug("Jwt Authentication Info - Extracted User Id from Access Token");

    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
        token,
        userId,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    log.debug("Security Context established");

    filterChain.doFilter(request, response);
  }
}
