package com.Ecommerce.OrderService.Security;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  private final UUID userId;
  private final String accessToken;

  public JwtAuthenticationToken(String accessToken, UUID userId, @Nullable Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.accessToken = accessToken;
    this.userId = userId;
    super.setAuthenticated(true);
  }

  @Override
  public @Nullable Object getCredentials() {
    return accessToken;
  }

  @Override
  public @Nullable Object getPrincipal() {
    return userId;
  }
}
