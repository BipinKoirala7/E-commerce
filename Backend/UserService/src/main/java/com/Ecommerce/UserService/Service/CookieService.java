package com.Ecommerce.UserService.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Cookie Service for User Service. Handles cookie
 * creation, deletion and fetching cookie.
 * */
@Slf4j
@Service
@Getter
public class CookieService {

  private final String ACCESS_TOKEN = "ACCESS_TOKEN";
  private final String REFRESH_TOKEN = "REFRESH_TOKEN";

  @Value("${app.cookie.refreshToken.path:/api}")
  private String REFRESH_TOKEN_PATH;

  @Value("${app.cookie.accessToken.path:/}")
  private String ACCESS_TOKEN_PATH;

  @Value("${app.cookie.httpOnly:true}")
  private Boolean httpOnly;

  @Value("${app.cookie.secure:true}")
  private Boolean secure;

  @Value("${app.accessTokenExpiration:3600}")
  private long accessTokenExpiration;

  @Value("${app.refreshTokenExpiration:259200}")
  private long refreshTokenExpiration;

  public Cookie createRefreshTokenCookie(@NonNull String token) {
    return createCookie(REFRESH_TOKEN, token, REFRESH_TOKEN_PATH, (int) refreshTokenExpiration);
  }

  public Cookie createAccessTokenCookie(@NonNull String token) {
    return createCookie(ACCESS_TOKEN, token, ACCESS_TOKEN_PATH, (int) accessTokenExpiration);
  }

  public Cookie getCookie(@NonNull HttpServletRequest request, @NonNull String cookieName) {
    log.debug("Fetching Cookie...");
    Cookie[] cookies = request.getCookies();

    if (Objects.isNull(cookies)) {
      log.debug("Fetching Cookie Failed - Request doesn't have any cookies");
      return null;
    }
    log.debug("Fetching Cookie Info - Cookies Found");

    for (Cookie cookie : cookies) {
      log.debug("Cookie Name : {}", cookie.getName());
      log.debug("Cookie Value : {}", cookie.getValue());
      if (cookie.getName().equals(cookieName)) {
        log.debug("Fetching Cookie Success for Cookie: {}", cookieName);
        return cookie;
      }
    }
    log.debug("Fetching Cookie Failed - Cookie with given name Not Found");
    return null;
  }

  public void deleteRefreshTokenCookie(@NonNull HttpServletResponse response) {
    Cookie cookie = createCookie(REFRESH_TOKEN,null, REFRESH_TOKEN_PATH, 0);
    response.addCookie(cookie);
  }

  public void deleteAccessTokenCookie(@NonNull HttpServletResponse response) {
    Cookie cookie = createCookie(ACCESS_TOKEN, null, ACCESS_TOKEN_PATH, 0);
    response.addCookie(cookie);
  }

  // Helper Methods
  private @NonNull Cookie createCookie(String name, String value, String path, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath(path);
    cookie.setHttpOnly(httpOnly);
    cookie.setSecure(secure);
    cookie.setAttribute("SameSite", "lax");
    cookie.setMaxAge(maxAge);

    return cookie;
  }
}
