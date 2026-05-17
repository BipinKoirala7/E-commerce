package com.Ecommerce.ProductService.DTOs;

import lombok.Getter;

@Getter
public enum HttpResponseMessage {

  OK_200("Done!"),
  CREATED_201("Created successfully!"),
  DELETED_204("Deleted successfully!"),

  BAD_REQUEST_400("Invalid input. Please check your details."),
  UNAUTHORIZED_401("Please log in to continue."),
  FORBIDDEN_403("You don't have permission to do this."),
  NOT_FOUND_404("Not found."),
  CONFLICT_409("Already exists. Try a different one."),
  UNPROCESSABLE_422("Could not process your request."),
  TOO_MANY_REQUESTS_429("Too many requests. Slow down!"),

  GENERAL_500("Something went wrong. Try again!"),
  SERVICE_UNAVAILABLE_503("Service unavailable. Try again later.");

  private final String message;

  HttpResponseMessage(String message) {
    this.message = message;
  }
}
