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
 * */
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
    log.debug("User Creation Info - User Info is present");

    User newUser = userMapper.fromCreateDto(userCreateDTO);
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    log.debug("User Creation Info - Password is encoded");

    if (userRepository.existsByEmail(newUser.getEmail())) {
      log.warn("User Creation Failed - User with given email already exists");
      throw new UserAlreadyExistsException("User Already Exists");
    }
    log.debug("User Creation Info - User with given email doesn't exist");

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
    log.debug("OAuth User Creation Info - OAuth User Info is present");

    if (userRepository.existsByEmailAndProviderId(oAuthUserCreateDTO.getEmail(), oAuthUserCreateDTO.getProviderId())) {
      log.warn("OAuth User Creation Failed - OAuth User with given email already exists");
      throw new UserAlreadyExistsException("User Already Exists");
    }
    log.debug("OAuth User Creation Info - OAuth User with given email doesn't exist");

    User newUser = userRepository.save(userMapper.fromOAuthCreateDto(oAuthUserCreateDTO));
    log.info("OAuth User Creation Success");
    return newUser;
  }

  public UserResponseDTO getUser() {
    log.info("Fetching User...");

    if (!userRepository.existsById(Objects.requireNonNull(SecurityUtils.getCurrentUserId()))) {
      log.info("Fetching User Failed - User Not Found");
      throw new UserNotFoundException("User Not Found");
    }
    log.debug("Fetching User Info - User Exists");

    User user = userRepository.findById(SecurityUtils.getCurrentUserId())
        .orElseThrow(() -> {
          log.warn("Fetching User Failed- User Not Found");
          return new IllegalArgumentException("User not found");
        });
    log.info("Fetching User Success");
    return userMapper.toResponseDTO(user);
  }

  @Transactional
  public void updateUser(UserUpdateDTO userUpdateDTO) {
    log.info("User Update...");

    if (Objects.isNull(userUpdateDTO)) {
      log.warn("User Update Failed - Updated User cannot be null");
      throw new IllegalArgumentException("Updated User be null");
    }
    log.debug("User Update Info - Updated User is present");

    User user = userRepository.findById(Objects.requireNonNull(SecurityUtils.getCurrentUserId()))
        .orElseThrow(() -> {
          log.warn("User Update Failed - User doesn't exist");
          return new IllegalArgumentException("User not found");
        });
    log.debug("User Update Info - User Exists");

    userMapper.fromUpdateDTOtoEntity(userUpdateDTO, user);
    userRepository.save(user);
    log.info("User Update Success");
  }

  @Transactional
  public void deleteUser() {
    log.info("User Deletion...");

    if (!userRepository.existsById(Objects.requireNonNull(SecurityUtils.getCurrentUserId()))) {
      log.info("User Deletion Failed - User does not exist");
      throw new IllegalArgumentException("User does not exist");
    }
    log.debug("User Deletion Info - User Exists");

    userRepository.deleteById(SecurityUtils.getCurrentUserId());
    log.info("User Deletion Success");
  }

  //  Other Methods
  public void updateUserLastLoginAt(UUID userId) {
    log.debug("User Last Login Date Update");

    if (Objects.isNull(userId)) {
      log.debug("User Last Login Date Update Failed - User Id cannot be null");
      throw new IllegalArgumentException("User Id cannot be null");
    }
    log.debug("User Last Login Date Update Info - User Id present");

    userRepository.updateLastLoginDate(LocalDateTime.now(), userId);
    log.debug("User Last Login Date Update Success");
  }

  public User getUserByEmail(String email) {
    log.debug("Fetching User...");

    if (Objects.isNull(email)) {
      log.warn("Fetching User Failed - Email cannot be null");
      throw new IllegalArgumentException("Email cannot be null");
    }
    log.debug("Fetching User Info - Email is present");

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> {
          log.warn("Fetching User Failed - User with given email doesn't exists");
          return new UserNotFoundException("User Not Found");
        });
  }

  public User getUserById(UUID userId) {
    log.debug("Fetching User By UserId...");

    if (Objects.isNull(userId)) {
      log.warn("Fetching User Failed - User Id cannot be null");
      throw new IllegalArgumentException("User Id cannot be null");
    }
    log.debug("Fetching User Info - User Id is present");

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
