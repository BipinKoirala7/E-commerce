package com.Ecommerce.UserService.Integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.Ecommerce.UserService.DTOs.Request.UserLoginDTO;
import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("test_db")
      .withUsername("test_user")
      .withPassword("test_password")
      .withReuse(false);

  @Value("${app.gateway.secret}")
  String gatewaySecret;

  @Value("${app.service.secret}")
  String serviceSecret;

  final String userName = "bipin.koirala.7";
  final String email = "bipinkoirala2061@gmail.com";
  final String password = "Bipin@123";

  @Autowired
  TestRestTemplate restTemplate;

  static {
    postgreSQLContainer.start();
  }

  public HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Gateway-Secret", gatewaySecret);
    return headers;
  }

  public HttpHeaders getAuthenticatedHeaders(String token){
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Gateway-Secret", gatewaySecret);
    headers.setBearerAuth(token);
    return headers;
  }

  protected String loginAndGetAuthToken(String email, String password) {
    UserLoginDTO userLoginDto = new UserLoginDTO();
    userLoginDto.setEmail(email);
    userLoginDto.setPassword(password);

    HttpHeaders headers = getHeaders();

    HttpEntity<UserLoginDTO> httpEntity = new HttpEntity<>(userLoginDto, headers);

    ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
        "/auth/login",
        HttpMethod.POST,
        httpEntity,
        new ParameterizedTypeReference<>() {
        });

    List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

    assertThat(cookies).isNotNull();
    assertThat(cookies).isNotEmpty();

    String ACCESS_TOKEN = "ACCESS_TOKEN";

    return cookies
        .stream()
        .filter(c -> c.startsWith(ACCESS_TOKEN))
        .findFirst()
        .map(c -> c.split(";", 2)[0].substring(ACCESS_TOKEN.length() + 1))
        .orElseThrow(() -> new IllegalStateException("Access Token cookie not found in response"));
  }

}
