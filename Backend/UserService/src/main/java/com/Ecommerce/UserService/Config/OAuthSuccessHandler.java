package com.Ecommerce.UserService.Config;

import com.Ecommerce.UserService.DTOs.Request.OAuthUserCreateDTO;
import com.Ecommerce.UserService.Model.AuthProvider;
import com.Ecommerce.UserService.Model.Role;
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Service.AuthService;
import com.Ecommerce.UserService.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/*
* Success Handler for OAuth
* */
@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserService userService;
  private final AuthService authService;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  public void onAuthenticationSuccess(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Authentication authentication) throws IOException {

    log.debug("OAuth Authentication Success Handler...");
    OAuth2User user = (OAuth2User) authentication.getPrincipal();

    if (Objects.isNull(user)) {
      log.debug("User object is null");
      response.sendRedirect(frontendUrl + "/login?error=auth_failed");
      return;
    }
    log.debug("OAuth Success Handler Info - User object is present");

    //  Provider info to be converted to User Info
    String googleId = user.getAttribute("sub");
    String email = user.getAttribute("email");
    String givenName = user.getAttribute("given_name");
    String familyName = user.getAttribute("family_name");
    String pictureUrl = user.getAttribute("picture");

    if (Objects.isNull(email)) {
      log.warn("OAuth Success Handler Failed - Email cannot be null");
      response.sendRedirect(frontendUrl + "/login?error=auth_failed");
      return;
    }
    log.debug("OAuth Success Handler Info - Email is present");

    if (Objects.isNull(googleId)) {
      log.warn("OAuth Success Handler Failed - Google Id cannot be null");
      response.sendRedirect(frontendUrl + "/login?error=auth_failed");
      return;
    }
    log.debug("OAuth Success Handler Info - Google Id is present");

    if (userService.existsByEmailAndProviderId(email, googleId)) {
      log.debug("OAuth Success Handler Info - User with given email already exists");
      log.debug("OAuth Success Handler Info - Logging in user into existing account");
      authService.loginOAuthUser(email, googleId, response);

      response.sendRedirect(frontendUrl);
      return;
    }

    //  Create a new User
    OAuthUserCreateDTO authUserCreateDTO = new OAuthUserCreateDTO(
        givenName + familyName,
        email,
        googleId,
        pictureUrl
    );

    User newUser = authService.registerOAuthUser(authUserCreateDTO);
    log.debug("OAuth Success Handler Info - New User registered");
    authService.loginOAuthUser(newUser.getEmail(), newUser.getProviderId(), response);
    log.debug("OAuth Success Handler Info - User logged in");

    response.sendRedirect(frontendUrl + "/account");
  }
}
