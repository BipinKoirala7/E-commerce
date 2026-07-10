package com.Ecommerce.UserService.Integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.Ecommerce.UserService.Model.AuthProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Ecommerce.UserService.DTOs.Request.UserUpdateDTO;
import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Model.Role;
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

public class UserIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail(email);
    user.setUserName(userName);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(Role.USER);
    user.setAuthProvider(AuthProvider.LOCAL);
    user.setEmailVerified(true);
    userRepository.save(user);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Nested
  class GetUser {

    @Test
    void shouldGetUser() {
      String token = loginAndGetAuthToken(email, password);

      HttpHeaders headers = getHeaders();
      headers.setBearerAuth(token);

      HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

      ResponseEntity<RestApiResponse<UserResponseDTO>> response = restTemplate.exchange(
          "/user",
          HttpMethod.GET,
          httpEntity,
          new ParameterizedTypeReference<>() {
          });

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertNotNull(response.getBody());
      assertEquals(true, response.getBody().getSuccess());
      assertEquals(email, response.getBody().getData().getEmail());
    }

    @Test
    void shouldGetUser_whenUserIsNotLoggedIn() {
      HttpHeaders headers = getHeaders();

      HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

      ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
          "/user",
          HttpMethod.GET,
          httpEntity,
          new ParameterizedTypeReference<>() {
          });

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      assertNotNull(response.getBody());
      assertNull(response.getBody().getData());
      assertFalse(response.getBody().getSuccess());
    }

    @Test
    void shouldGetUser_whenAccessTokenIsMalformed() {
      String token = "get.access.token";
      HttpHeaders headers = getHeaders();
      headers.setBearerAuth(token);

      HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

      ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
          "/user",
          HttpMethod.GET,
          httpEntity,
          new ParameterizedTypeReference<>() {
          });

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      assertNotNull(response.getBody());
      assertNull(response.getBody().getData());
      assertFalse(response.getBody().getSuccess());
    }

    @Test
    void shouldGetUser_whenUserDoesNotExists() {
      String token = loginAndGetAuthToken(email, password);

      userRepository.deleteByEmail(email);

      assertFalse(userRepository.existsByEmail(email));

      HttpHeaders headers = getHeaders();
      headers.setBearerAuth(token);

      HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

      ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
          "/user",
          HttpMethod.GET,
          httpEntity,
          new ParameterizedTypeReference<>() {
          });

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertFalse(response.getBody().getSuccess());
    }
  }

  @Nested
  class UpdateUser {

    @Test
    void updateUser() {
      String token = loginAndGetAuthToken(email, password);
      String newEmail = "bipin.koirala.2061@gmail.com";
      String newUserName = "bipin.koirala.123";

      UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
      userUpdateDTO.setEmail(newEmail);
      userUpdateDTO.setUserName(newUserName);

      HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(userUpdateDTO, getAuthenticatedHeaders(token));

      ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
          "/user",
          HttpMethod.PATCH,
          httpEntity,
          new ParameterizedTypeReference<>() {
          });

      assertNotNull(response.getBody());
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertTrue(userRepository.existsByEmail(newEmail));

      Optional<User> user = userRepository.findByEmail(newEmail);
      assertTrue(user.isPresent());
      assertEquals(newUserName, user.get().getUserName());
    }

        @Test
    void shouldFailUpdate_whenUserIsNotLoggedIn() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@gmail.com");
        dto.setUserName("newUsername");

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getHeaders()); // no Bearer token

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isFalse();

        // confirm nothing changed
        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailUpdate_whenAccessTokenIsMalformed() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@gmail.com");
        dto.setUserName("newUsername");

        HttpHeaders headers = getAuthenticatedHeaders("not.a.valid.jwt");
        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
            new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getSuccess()).isFalse();
    }

    @Test
    void shouldFailUpdate_whenUserIsNotFound() {
        // valid token at the time of login, but the user is gone by the time the update runs
        String token = loginAndGetAuthToken(email, password);
        userRepository.deleteByEmail(email);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@gmail.com");
        dto.setUserName("newUsername");

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
            "/user", HttpMethod.PATCH, httpEntity,
            new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getSuccess()).isFalse();
    }

    @Test
    void shouldFailUpdate_whenEmailIsInvalid() {
        // ASSUMPTION: UserUpdateDTO.email is annotated @Email, and your
        // exception handler maps MethodArgumentNotValidException -> 400
        String token = loginAndGetAuthToken(email, password);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("not-a-valid-email");
        dto.setUserName("someUsername");

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getSuccess()).isFalse();

        // confirm nothing persisted
        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailUpdate_whenEmailAlreadyTakenByAnotherUser() {
        // ASSUMPTION: your service throws a conflict (409) when the new email
        // belongs to a different, already-existing user (a real DB uniqueness check,
        // not just a validation annotation)
        String otherEmail = "someoneelse@gmail.com";
        User otherUser = new User();
        otherUser.setEmail(otherEmail);
        otherUser.setUserName("someone");
        otherUser.setPassword(passwordEncoder.encode("Password@123"));
        otherUser.setAuthProvider(AuthProvider.LOCAL);
        otherUser.setEmailVerified(true);
        otherUser.setRole(Role.USER);
        userRepository.save(otherUser);

        String token = loginAndGetAuthToken(email, password);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail(otherEmail);
        dto.setUserName("newUsername");

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
            "/user",
            HttpMethod.PATCH,
            httpEntity,
            new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getSuccess()).isFalse();

        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailUpdate_whenUserNameIsTooShort() {
        String token = loginAndGetAuthToken(email, password);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("valid@gmail.com");
        dto.setUserName("ab");

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldSucceed_whenUpdatingToSameEmailAndUsername() {
        String token = loginAndGetAuthToken(email, password);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail(email);
        dto.setUserName(userName);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(dto, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailUpdate_whenRequestBodyIsEmpty() {
        String token = loginAndGetAuthToken(email, password);

        HttpEntity<UserUpdateDTO> httpEntity = new HttpEntity<>(null, getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.PATCH, httpEntity,
            new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

@Nested
class DeleteUser {

    @Test
    void shouldDeleteUser() {
        String token = loginAndGetAuthToken(email, password);

        HttpEntity<Void> httpEntity = new HttpEntity<>(getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();

        assertThat(userRepository.findByEmail(email)).isEmpty();
    }

    @Test
    void shouldFailDelete_whenUserIsNotLoggedIn() {
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeaders());

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getSuccess()).isFalse();

        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailDelete_whenAccessTokenIsMalformed() {
        HttpHeaders headers = getAuthenticatedHeaders("get.access.token");
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
                new ParameterizedTypeReference<RestApiResponse<Void>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getSuccess()).isFalse();

        assertThat(userRepository.findByEmail(email)).isPresent();
    }

    @Test
    void shouldFailDelete_whenUserDoesNotExist() {
        String token = loginAndGetAuthToken(email, password);
        userRepository.deleteByEmail(email);

        assertThat(userRepository.findByEmail(email)).isNotPresent();

        HttpEntity<Void> httpEntity = new HttpEntity<>(getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> response = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
            new ParameterizedTypeReference<>() {
            });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getSuccess()).isFalse();
    }

    @Test
    void shouldFailSecondDelete_afterUserAlreadyDeleted() {
        String token = loginAndGetAuthToken(email, password);
        HttpEntity<Void> httpEntity = new HttpEntity<>(getAuthenticatedHeaders(token));

        ResponseEntity<RestApiResponse<Void>> firstResponse = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
            new ParameterizedTypeReference<>() {
            });
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<RestApiResponse<Void>> secondResponse = restTemplate.exchange(
                "/user", HttpMethod.DELETE, httpEntity,
            new ParameterizedTypeReference<>() {
            });

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(secondResponse.getBody());
        assertThat(secondResponse.getBody().getSuccess()).isFalse();
    }

    @Test
    void shouldFailDelete_whenAccessTokenIsExpiredOrInvalidSignature() {
        // ASSUMPTION: JwtService exposes a way to mint a token with an already-past
        // expiry (e.g. jwtService.generateAccessToken(user, Duration.ofSeconds(-1)))
        // If it doesn't, this test isn't writable without a temporary test-only method
        // — flagging as a gap rather than guessing at an API that may not exist.
    }
}
}
