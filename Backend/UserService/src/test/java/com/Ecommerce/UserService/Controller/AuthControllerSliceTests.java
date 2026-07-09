package com.Ecommerce.UserService.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.Ecommerce.UserService.Config.AuthConfig;
import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserLoginDTO;
import com.Ecommerce.UserService.Exception.EmptyTokenException;
import com.Ecommerce.UserService.Exception.IncorrectEmailOrPasswordException;
import com.Ecommerce.UserService.Exception.InValidTokenException;
import com.Ecommerce.UserService.Exception.UserAlreadyExistsException;
import com.Ecommerce.UserService.Exception.UserNotFoundException;
import com.Ecommerce.UserService.Service.AuthService;
import com.Ecommerce.UserService.Service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import(AuthConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerSliceTests {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;

  @MockitoBean
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;


  private UserCreateDTO validCreateDto() {
    UserCreateDTO dto = new UserCreateDTO();
    dto.setEmail("bipin.koirala.123@gmail.com");
    dto.setPassword("BipinPass!123");
    dto.setUserName("bipin.koirala123");
    return dto;
  }

  private UserLoginDTO validLoginDto() {
    UserLoginDTO dto = new UserLoginDTO();
    dto.setEmail("bipin.koirala.123@gmail.com");
    dto.setPassword("BipinPass!123");
    return dto;
  }

  private org.springframework.test.web.servlet.ResultActions postJson(String path, Object body) throws Exception {
    return mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(body)));
  }

  @Nested
  class RegisterUser {

    @Test
    void shouldRegisterUser_whenUserInfoIsValid() throws Exception {
      postJson("/auth/register", validCreateDto())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.statusCode").value(201))
          .andExpect(jsonPath("$.message").value("User Created!"))
          .andExpect(jsonPath("$.data").doesNotExist())
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturn400_whenRequestBodyIsMissing() throws Exception {
      mockMvc.perform(post("/auth/register"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.statusCode").value(400));

      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenRequestBodyIsMalformedJson() throws Exception {
      mockMvc.perform(post("/auth/register")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{ this is not valid json"))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenEmailIsNull() throws Exception {
      UserCreateDTO dto = validCreateDto();
      dto.setEmail(null);

      postJson("/auth/register", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenEmailIsInvalid() throws Exception {
      UserCreateDTO dto = validCreateDto();
      dto.setEmail("not-an-email");

      postJson("/auth/register", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenPasswordIsBlank() throws Exception {
      UserCreateDTO dto = validCreateDto();
      dto.setPassword("");

      postJson("/auth/register", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenUsernameIsBlank() throws Exception {
      UserCreateDTO dto = validCreateDto();
      dto.setUserName("");

      postJson("/auth/register", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn409_whenUserAlreadyExists() throws Exception {
      doThrow(new UserAlreadyExistsException("User Already Exists"))
          .when(authService).registerUser(any(UserCreateDTO.class));

      postJson("/auth/register", validCreateDto())
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.statusCode").value(409))
          .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturn500_whenServiceThrowsUnexpectedException() throws Exception {
      doThrow(new RuntimeException("db connection lost"))
          .when(authService).registerUser(any(UserCreateDTO.class));

      postJson("/auth/register", validCreateDto())
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.statusCode").value(500))
          .andExpect(jsonPath("$.success").value(false));
    }
  }

  @Nested
  class LoginUser {

    @Test
    void shouldLoginUser_whenCredentialsAreValid() throws Exception {
      postJson("/auth/login", validLoginDto())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.statusCode").value(200))
          .andExpect(jsonPath("$.message").value("Logged In!"))
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturn404_whenUserDoesNotExist() throws Exception {
      doThrow(new UserNotFoundException("User doesn't exist"))
          .when(authService).loginUser(any(UserLoginDTO.class), any());

      postJson("/auth/login", validLoginDto())
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.statusCode").value(404))
          .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturn401_whenPasswordIsIncorrect() throws Exception {
      doThrow(new IncorrectEmailOrPasswordException("Email or Password is incorrect"))
          .when(authService).loginUser(any(UserLoginDTO.class), any());

      postJson("/auth/login", validLoginDto())
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.statusCode").value(401))
          .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturn400_whenEmailIsNull() throws Exception {
      UserLoginDTO dto = validLoginDto();
      dto.setEmail(null);

      postJson("/auth/login", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenPasswordIsBlank() throws Exception {
      UserLoginDTO dto = validLoginDto();
      dto.setPassword("");

      postJson("/auth/login", dto).andExpect(status().isBadRequest());
      verifyNoInteractions(authService);
    }

    @Test
    void shouldReturn400_whenRequestBodyIsMissing() throws Exception {
      mockMvc.perform(post("/auth/login"))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(authService);
    }
  }

  @Nested
  class Logout {

    @Test
    void shouldLogoutUser_whenRefreshTokenCookieIsValid() throws Exception {
      mockMvc.perform(post("/auth/logout"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.statusCode").value(200))
          .andExpect(jsonPath("$.message").value("Logged Out!"))
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturn401_whenRefreshTokenCookieIsMissing() throws Exception {
      doThrow(new EmptyTokenException("Refresh Token is empty"))
          .when(authService).logout(any(), any());

      mockMvc.perform(post("/auth/logout"))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.statusCode").value(401))
          .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturn401_whenRefreshTokenIsInvalid() throws Exception {
      doThrow(new InValidTokenException("Invalid token"))
          .when(authService).logout(any(), any());

      mockMvc.perform(post("/auth/logout"))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.statusCode").value(401))
          .andExpect(jsonPath("$.success").value(false));
    }
  }
}