package com.Ecommerce.UserService.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Ecommerce.UserService.DTOs.Request.OAuthUserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserUpdateDTO;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Exception.UserAlreadyExistsException;
import com.Ecommerce.UserService.Exception.UserNotFoundException;
import com.Ecommerce.UserService.Mapper.UserMapper;
import com.Ecommerce.UserService.Model.AuthProvider;
import com.Ecommerce.UserService.Model.Role;
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Repository.UserRepository;
import com.Ecommerce.UserService.Security.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @InjectMocks
  private UserService underTest;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  private final UserCreateDTO userCreateDTO = new UserCreateDTO();
  private final OAuthUserCreateDTO oAuthUserCreateDTO = new OAuthUserCreateDTO("bipinkoirala7",
      "bipin.koiral@google.com",
      "Google|12345", null);

  private final User user = new User();
  private final User oAuthUser = new User();

  @BeforeEach
  void beforeEach() {
    userCreateDTO.setUserName("bipinkoirala7");
    userCreateDTO.setEmail("bipin.koiral@gmail.com");
    userCreateDTO.setPassword("BipinPass!123");
    userCreateDTO.setProfilePictureUrl(null);

    user.setId(UUID.randomUUID());
    user.setUserName(userCreateDTO.getUserName());
    user.setEmail(userCreateDTO.getEmail());
    user.setPassword(userCreateDTO.getPassword());
    user.setAuthProvider(AuthProvider.LOCAL);
    user.setProviderId(null);
    user.setEmailVerified(false);
    user.setRole(Role.USER);
    user.setProfilePictureUrl(null);
    user.setLastLoginAt(null);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    oAuthUser.setId(UUID.randomUUID());
    oAuthUser.setUserName(oAuthUserCreateDTO.getUserName());
    oAuthUser.setEmail(oAuthUserCreateDTO.getEmail());
    oAuthUser.setPassword(null);
    oAuthUser.setAuthProvider(AuthProvider.GOOGLE);
    oAuthUser.setProviderId(oAuthUserCreateDTO.getProviderId());
    oAuthUser.setEmailVerified(false);
    oAuthUser.setRole(Role.USER);
    oAuthUser.setProfilePictureUrl(null);
    oAuthUser.setLastLoginAt(null);
    oAuthUser.setCreatedAt(LocalDateTime.now());
    oAuthUser.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void createNewUser() {
    when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(false);
    when(userMapper.fromCreateDto(userCreateDTO)).thenReturn(user);
    when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(user)).thenReturn(user);

    underTest.createNewUser(userCreateDTO);

    verify(userRepository).existsByEmail(userCreateDTO.getEmail());
    verify(userMapper).fromCreateDto(userCreateDTO);
    verify(passwordEncoder).encode(userCreateDTO.getPassword());
    assertEquals("encodedPassword", user.getPassword());
    verify(userRepository).save(user);
  }

  @Test
  void createNewUser_whenUserCreateDtoIsNull() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> underTest.createNewUser(null));
    assertEquals("User cannot be null", e.getMessage());
  }

  @Test
  void createNewUser_whenEmailAlreadyExists() {

    when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(true);

    UserAlreadyExistsException e = assertThrows(UserAlreadyExistsException.class,
        () -> underTest.createNewUser(userCreateDTO));
    assertEquals("User Already Exists", e.getMessage());

    verify(userRepository).existsByEmail(userCreateDTO.getEmail());
  }

  @Test
  void createNewOAuthUser() {

    when(userRepository.existsByEmailAndProviderId(oAuthUserCreateDTO.getEmail(), oAuthUserCreateDTO.getProviderId()))
        .thenReturn(false);
    when(userMapper.fromOAuthCreateDto(oAuthUserCreateDTO)).thenReturn(oAuthUser);
    when(userRepository.save(oAuthUser)).thenReturn(oAuthUser);

    underTest.createNewOAuthUser(oAuthUserCreateDTO);

    verify(userRepository).existsByEmailAndProviderId(oAuthUser.getEmail(), oAuthUser.getProviderId());
    verify(userMapper).fromOAuthCreateDto(oAuthUserCreateDTO);
    verify(userRepository).save(oAuthUser);
  }

  @Test
  void createNewOAuthUser_whenUserCreateDtoIsNull() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> underTest.createNewOAuthUser(null));
    assertEquals("OAuth User cannot be null", e.getMessage());
  }

  @Test
  void createNewOAuthUser_whenEmailAlreadyExists() {
    when(userRepository.existsByEmailAndProviderId(oAuthUserCreateDTO.getEmail(), oAuthUserCreateDTO.getProviderId()))
        .thenReturn(true);

    UserAlreadyExistsException e = assertThrows(UserAlreadyExistsException.class,
        () -> underTest.createNewOAuthUser(oAuthUserCreateDTO));
    assertEquals("User Already Exists", e.getMessage());

    verify(userRepository).existsByEmailAndProviderId(oAuthUserCreateDTO.getEmail(),
        oAuthUserCreateDTO.getProviderId());
  }

  @Test
  void getUser() {
    UserResponseDTO responseDTO = new UserResponseDTO();
    responseDTO.setUserName(user.getUserName());
    responseDTO.setEmail(user.getEmail());
    responseDTO.setRole(user.getRole());

    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);
      UserResponseDTO result = underTest.getUser();

      assertEquals(user.getEmail(), result.getEmail());

      verify(userRepository).findById(user.getId());
      verify(userMapper).toResponseDTO(user);
    }
  }

  @Test
  void getUser_whenUserIsNull() {
    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

      UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> underTest.getUser());
      assertEquals("User not found", e.getMessage());

      verify(userRepository).findById(user.getId());
    }
  }

  @Test
  void updateUser() {
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
    userUpdateDTO.setUserName("bipin123");
    userUpdateDTO.setEmail("bipin.koirala.123@gmail.com");
    userUpdateDTO.setEmailVerified(true);
    userUpdateDTO.setRole(null);
    userUpdateDTO.setProfilePictureUrl(null);

    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      user.setUserName(userUpdateDTO.getUserName());
      user.setEmail(userUpdateDTO.getEmail());
      user.setEmailVerified(userUpdateDTO.getEmailVerified());
      when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

      underTest.updateUser(userUpdateDTO);

      assertEquals(userUpdateDTO.getUserName(), user.getUserName());
      assertEquals(userUpdateDTO.getEmail(), user.getEmail());
      assertEquals(userUpdateDTO.getEmailVerified(), user.getEmailVerified());

      verify(userRepository).findById(user.getId());
      verify(userMapper).fromUpdateDTOtoEntity(userUpdateDTO, user);
      verify(userRepository).save(user);
    }
  }

  @Test
  void updateUser_whenUserUpdateDtoIsNull() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> underTest.updateUser(null));
    assertEquals("Updated User cannot be null", e.getMessage());
  }

  @Test
  void updateUser_WhenUserIsNotFound() {
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
    userUpdateDTO.setUserName("bipin123");
    userUpdateDTO.setEmail("bipin.koirala.123@gmail.com");
    userUpdateDTO.setEmailVerified(true);
    userUpdateDTO.setRole(null);
    userUpdateDTO.setProfilePictureUrl(null);

    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

      UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> underTest.updateUser(userUpdateDTO));
      assertEquals("User not found", e.getMessage());

      verify(userRepository).findById(user.getId());
    }
  }

  @Test
  void deleteUser() {
    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      when(userRepository.existsById(user.getId())).thenReturn(true);

      underTest.deleteUser();

      verify(userRepository).existsById(user.getId());
      verify(userRepository).deleteById(user.getId());
    }
  }

  @Test
  void deleteUser_whenUserNoLongerExists() {
    try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
      mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(user.getId());

      when(userRepository.existsById(user.getId())).thenReturn(false);

      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> underTest.deleteUser());
      assertEquals("User does not exists", e.getMessage());

      verify(userRepository).existsById(user.getId());
    }
  }
}
