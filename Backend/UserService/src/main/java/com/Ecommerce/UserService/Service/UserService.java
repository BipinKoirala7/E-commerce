package com.Ecommerce.UserService.Service;

import com.Ecommerce.UserService.DTOs.Request.OAuthUserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserCreateDTO;
import com.Ecommerce.UserService.DTOs.Request.UserUpdateDTO;
import com.Ecommerce.UserService.DTOs.Response.UserResponseDTO;
import com.Ecommerce.UserService.Exception.UserAlreadyExistsException;
import com.Ecommerce.UserService.Exception.UserNotFoundException;
import com.Ecommerce.UserService.Mapper.UserMapper;
import com.Ecommerce.UserService.Model.User;
import com.Ecommerce.UserService.Repository.UserRepository;
import com.Ecommerce.UserService.Security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service that handles creating, updating and deleting user.
 *
 * @see UserRepository
 * @see UserMapper
 * @see UserCreateDTO
 * @see OAuthUserCreateDTO
 * @see UserUpdateDTO
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  public void createNewUser(UserCreateDTO userCreateDTO) {
    log.info("User Creation...");

    if (Objects.isNull(userCreateDTO)) {
      log.warn("User Creation Failed - User cannot be null");
      throw new IllegalArgumentException("User cannot be null");
    }
    if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
      log.warn("User Creation Failed - User with given email already exists");
      throw new UserAlreadyExistsException("User Already Exists");
    }

    User newUser = userMapper.fromCreateDto(userCreateDTO);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    userRepository.save(newUser);
    log.info("User Creation Success");
  }

  @Transactional
  public User createNewOAuthUser(OAuthUserCreateDTO oAuthUserCreateDTO) {
    log.info("OAuth User Creation...");

    if (Objects.isNull(oAuthUserCreateDTO)) {
      log.warn("OAuth User Creation Failed - OAuth User cannot be null");
      throw new IllegalArgumentException("OAuth User cannot be null");
    }
    if (userRepository.existsByEmailAndProviderId(oAuthUserCreateDTO.getEmail(), oAuthUserCreateDTO.getProviderId())) {
      log.warn("OAuth User Creation Failed - OAuth User with given email already exists");
      throw new UserAlreadyExistsException("User Already Exists");
    }

    User newUser = userRepository.save(userMapper.fromOAuthCreateDto(oAuthUserCreateDTO));
    log.info("OAuth User Creation Success");
    return newUser;
  }

  public UserResponseDTO getUser() {
    log.info("Fetching User...");
    User user = userRepository.findById(SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> {
          log.warn("Fetching User Failed- User Not Found");
          return new UserNotFoundException("User not found");
        });
    log.info("Fetching User Success");
    return userMapper.toResponseDTO(user);
  }

  @Transactional
  public void updateUser(UserUpdateDTO userUpdateDTO) {
    log.info("User Update...");

    if (Objects.isNull(userUpdateDTO)) {
      log.warn("User Update Failed - Updated User cannot be null");
      throw new IllegalArgumentException("Updated User cannot be null");
    }

    User user = userRepository.findById(Objects.requireNonNull(SecurityUtils.getCurrentUserId()))
        .orElseThrow(() -> {
          log.warn("User Update Failed - User doesn't exist");
          return new UserNotFoundException("User not found");
        });

    userMapper.fromUpdateDTOtoEntity(userUpdateDTO, user);
    userRepository.save(user);
    log.info("User Update Success");
  }

  @Transactional
  public void deleteUser() {
    log.info("User Deletion...");

    if (!userRepository.existsById(Objects.requireNonNull(SecurityUtils.getCurrentUserId()))) {
      log.info("User Deletion Failed - User does not exists");
      throw new IllegalArgumentException("User does not exists");
    }

    userRepository.deleteById(SecurityUtils.getCurrentUserId());
    log.info("User Deletion Success");
  }

  //  Other Methods
  public void updateUserLastLoginAt(UUID userId) {
    if (Objects.isNull(userId)) {
      log.debug("User Last Login Date Update Failed - User Id cannot be null");
      throw new IllegalArgumentException("User Id cannot be null");
    }

    userRepository.updateLastLoginDate(LocalDateTime.now(), userId);
    log.debug("User Last Login Date Update Success");
  }

  public User getUserByEmail(String email) {
    if (Objects.isNull(email)) {
      log.warn("Fetching User Failed - Email cannot be null");
      throw new IllegalArgumentException("Email cannot be null");
    }

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> {
          log.warn("Fetching User Failed - User with given email doesn't exists");
          return new UserNotFoundException("User Not Found");
        });
  }

  public User getUserById(UUID userId) {
    if (Objects.isNull(userId)) {
      log.warn("Fetching User Failed - User Id cannot be null");
      throw new IllegalArgumentException("User Id cannot be null");
    }

    return userRepository
        .findById(userId)
        .orElseThrow(() -> {
          log.warn("Fetching User Failed - User doesn't exist");
          return new UserNotFoundException("User with given id not found");
        });
  }

  public boolean existsByEmailAndProviderId(String email, String providerId) {
    return userRepository.existsByEmailAndProviderId(email, providerId);
  }
}
