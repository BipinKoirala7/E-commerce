package com.Ecommerce.UserService.Filters;

import com.Ecommerce.UserService.DTOs.Response.RestApiResponse;
import com.Ecommerce.UserService.Exception.EmptyTokenException;
import com.Ecommerce.UserService.Exception.InValidTokenException;
import com.Ecommerce.UserService.Exception.UnVerifiedSourceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Establishes Exception Handling for Filter level.
 *
 * @see EmptyTokenException
 * @see InValidTokenException
 * @see UnVerifiedSourceException
 * **/
@Slf4j
@RequiredArgsConstructor
@Component
public class FilterExceptionHandler extends OncePerRequestFilter {
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      MDC.put("RequestId", UUID.randomUUID().toString());
      MDC.put("Method", request.getMethod());
      MDC.put("path", request.getServletPath());

      log.debug("Request Started");
      log.debug("Filter Exception Handler...");

      filterChain.doFilter(request, response);
    } catch (EmptyTokenException e) {
      log.error("Empty Token Exception Occurred");
      log.warn("Exception Message: {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (InValidTokenException e) {
      log.error("InValid Token Type Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (UnVerifiedSourceException e) {
      log.error("Source AuthenticationFailed Exception Occurred");
      log.warn("Exception Message: {}", e.getMessage());
      String message = "Access Denied";

      sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, message);
    } catch (ExpiredJwtException e) {
      log.error("Expired Jwt Exception occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (SignatureException e) {
      log.info("Signature Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (MalformedJwtException e) {
      log.info("Malformed Jwt Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported Jwt Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (JwtException e) {
      log.info("Jwt Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (ServletException | IOException e) {
      log.error("Error processing request: {}", e.getMessage());
      throw e;
    } catch (RuntimeException e) {
      log.error("Error with runtime exception: {}", e.getMessage());
      String message = "Something went wrong. Please try again!";

      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    } catch (Exception e) {
      log.error("Exception in filter chain: {}", e.getMessage());
      String message = "Something went wrong. Please try again!";

      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
  }

  private void sendErrorResponse(
      @NonNull HttpServletResponse response,
      int status,
      String message
  ) throws IOException {
    if (response.isCommitted()) {
      log.warn("Response already committed, cannot send error response");
      return;
    }

    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    RestApiResponse<Void> errorResponse = RestApiResponse.error(status, message);
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
