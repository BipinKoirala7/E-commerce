package com.Ecommerce.UserService.Security;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * Creates JWT filled authentication token class for Security Context.
 * */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  private final UUID userId;
  private final String accessToken;

  public JwtAuthenticationToken(String token, UUID userId, @Nullable Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.accessToken = token;
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
