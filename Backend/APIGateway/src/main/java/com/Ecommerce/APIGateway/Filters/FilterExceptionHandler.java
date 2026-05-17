package com.Ecommerce.APIGateway.Filters;

import com.Ecommerce.APIGateway.DTOs.RestApiResponse;
import com.Ecommerce.APIGateway.Exception.EmptyTokenException;
import com.Ecommerce.APIGateway.Exception.InValidTokenException;
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
      MDC.put("Path", request.getRequestURI());

      log.debug("Request Method: {}", request.getMethod());
      log.debug("Request Path: {}", request.getRequestURI());

      log.debug("Request Started");
      log.debug("Filter Exception Handler...");

      filterChain.doFilter(request, response);
    } catch (EmptyTokenException e) {
      log.warn("Empty Token Exception Occurred");
      log.warn("Exception Message: {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (InValidTokenException e) {
      log.warn("InValid Token Type Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (ExpiredJwtException e) {
      log.warn("Expired Jwt Exception occurred");
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
      log.warn("Unsupported Jwt Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (JwtException e) {
      log.warn("Jwt Exception Occurred");
      log.warn("Exception message : {}", e.getMessage());
      String message = "Authentication Failed. Please log in again!";

      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
    } catch (ServletException | IOException e) {
      throw e;
    } catch (Exception e) {
      log.warn("Exception in filter chain: {}", e.getMessage());
      log.warn("Exception: {}", e.getClass().getName());
      String message = "Something went wrong";

      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    } finally {
      MDC.clear();
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
