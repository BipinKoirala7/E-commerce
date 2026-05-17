package com.Ecommerce.UserService.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
* Authentication Failure handler for OAuth.
* */
@Component
@Slf4j
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  public void onAuthenticationFailure(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull AuthenticationException exception
  ) throws IOException {
    log.error("Authentication failed for OAuth with message: {}", exception.getMessage());

    response.sendRedirect(frontendUrl + "/error");
  }
}
