package com.Ecommerce.UserService.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class CookieServiceTests {

  private static final String REFRESH_TOKEN_PATH = "/api";
  private static final String ACCESS_TOKEN_PATH = "/";
  private static final boolean HTTP_ONLY = true;
  private static final boolean SECURE = true;
  private static final String SAME_SITE = "Strict";
  private static final long ACCESS_TOKEN_EXPIRATION = 3600L;
  private static final long REFRESH_TOKEN_EXPIRATION = 259200L;

  @InjectMocks
  private CookieService underTest;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(underTest, "REFRESH_TOKEN_PATH", REFRESH_TOKEN_PATH);
    ReflectionTestUtils.setField(underTest, "ACCESS_TOKEN_PATH", ACCESS_TOKEN_PATH);
    ReflectionTestUtils.setField(underTest, "httpOnly", HTTP_ONLY);
    ReflectionTestUtils.setField(underTest, "secure", SECURE);
    ReflectionTestUtils.setField(underTest, "sameSite", SAME_SITE);
    ReflectionTestUtils.setField(underTest, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
    ReflectionTestUtils.setField(underTest, "accessTokenExpiration", ACCESS_TOKEN_EXPIRATION);
  }

  @Nested
  class CreateRefreshTokenCookie {

    @Test
    void createsCookieWithExpectedNameValueAndAttributes() {
      Cookie cookie = underTest.createRefreshTokenCookie("some-refresh-token");

      assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
      assertThat(cookie.getValue()).isEqualTo("some-refresh-token");
      assertThat(cookie.getPath()).isEqualTo(REFRESH_TOKEN_PATH);
      assertThat(cookie.isHttpOnly()).isEqualTo(HTTP_ONLY);
      assertThat(cookie.getSecure()).isEqualTo(SECURE);
      assertThat(cookie.getAttribute("SameSite")).isEqualTo(SAME_SITE);
      assertThat(cookie.getMaxAge()).isEqualTo((int) REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    void nullToken_throwsNullPointerException() {
      assertThrows(NullPointerException.class,
          () -> underTest.createRefreshTokenCookie(null));
    }
  }

  @Nested
  class CreateAccessTokenCookie {

    @Test
    void createsCookieWithExpectedNameValueAndAttributes() {
      Cookie cookie = underTest.createAccessTokenCookie("some-access-token");

      assertThat(cookie.getName()).isEqualTo("ACCESS_TOKEN");
      assertThat(cookie.getValue()).isEqualTo("some-access-token");
      assertThat(cookie.getPath()).isEqualTo(ACCESS_TOKEN_PATH);
      assertThat(cookie.isHttpOnly()).isEqualTo(HTTP_ONLY);
      assertThat(cookie.getSecure()).isEqualTo(SECURE);
      assertThat(cookie.getAttribute("SameSite")).isEqualTo(SAME_SITE);
      assertThat(cookie.getMaxAge()).isEqualTo((int) ACCESS_TOKEN_EXPIRATION);
    }

    @Test
    void nullToken_throwsNullPointerException() {
      assertThrows(NullPointerException.class,
          () -> underTest.createAccessTokenCookie(null));
    }
  }

  @Nested
  class GetCookie {

    @Test
    void noCookiesOnRequest_returnsNull() {
      when(request.getCookies()).thenReturn(null);

      Cookie result = underTest.getCookie(request, "ACCESS_TOKEN");

      assertNull(result);
    }

    @Test
    void cookiesPresentButNameNotFound_returnsNull() {
      Cookie other = new Cookie("SOME_OTHER_COOKIE", "value");
      when(request.getCookies()).thenReturn(new Cookie[]{other});

      Cookie result = underTest.getCookie(request, "ACCESS_TOKEN");

      assertNull(result);
    }

    @Test
    void matchingCookieExists_returnsIt() {
      Cookie target = new Cookie("ACCESS_TOKEN", "abc123");
      Cookie other = new Cookie("SOME_OTHER_COOKIE", "value");
      when(request.getCookies()).thenReturn(new Cookie[]{other, target});

      Cookie result = underTest.getCookie(request, "ACCESS_TOKEN");

      assertThat(result).isNotNull();
      assertThat(result.getValue()).isEqualTo("abc123");
    }

    @Test
    void multipleCookiesWithDifferentNames_returnsCorrectOne() {
      Cookie refresh = new Cookie("REFRESH_TOKEN", "refresh-val");
      Cookie access = new Cookie("ACCESS_TOKEN", "access-val");
      when(request.getCookies()).thenReturn(new Cookie[]{refresh, access});

      Cookie result = underTest.getCookie(request, "REFRESH_TOKEN");

      assertThat(result.getValue()).isEqualTo("refresh-val");
    }
  }

  @Nested
  class DeleteRefreshTokenCookie {

    @Test
    void addsCookieWithZeroMaxAgeAndNullValue() {
      underTest.deleteRefreshTokenCookie(response);

      ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
      verify(response).addCookie(captor.capture());

      Cookie deleted = captor.getValue();
      assertThat(deleted.getName()).isEqualTo("REFRESH_TOKEN");
      assertThat(deleted.getValue()).isNull();
      assertThat(deleted.getPath()).isEqualTo(REFRESH_TOKEN_PATH);
      assertThat(deleted.getMaxAge()).isZero();
    }
  }

  @Nested
  class DeleteAccessTokenCookie {

    @Test
    void addsCookieWithZeroMaxAgeAndNullValue() {
      underTest.deleteAccessTokenCookie(response);

      ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
      verify(response).addCookie(captor.capture());

      Cookie deleted = captor.getValue();
      assertThat(deleted.getName()).isEqualTo("ACCESS_TOKEN");
      assertThat(deleted.getValue()).isNull();
      assertThat(deleted.getPath()).isEqualTo(ACCESS_TOKEN_PATH);
      assertThat(deleted.getMaxAge()).isZero();
    }
  }
}
