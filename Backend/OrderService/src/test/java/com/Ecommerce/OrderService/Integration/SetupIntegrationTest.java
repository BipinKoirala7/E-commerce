package com.Ecommerce.OrderService.Integration;

import com.Ecommerce.OrderService.Service.JwtService;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public abstract class SetupIntegrationTest {

  @Autowired
  private JwtService jwtService;

  private TokenGenerator tokenGenerator;

  @PostConstruct
  public void setup() {
    tokenGenerator = new TokenGenerator(jwtService, "obsidian.com", "api.obsidian.com");
  }

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("test_db")
      .withUsername("test_user")
      .withPassword("test_password")
      .withReuse(false);

  @Value("${app.gateway.secret}")
  String gatewaySecret;

  final UUID userId = UUID.randomUUID();
  final String email = "bipinkoirala2061@gmail.com";

  protected @NonNull HttpHeaders getAuthenticatedHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Gateway-Secret", gatewaySecret);
    headers.setBearerAuth(token);
    return headers;
  }

  protected String login(){
    return tokenGenerator.generateAccessToken(
      userId,
      email,
      "USER"
    );
  }
}
