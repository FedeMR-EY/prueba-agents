package com.ey.app.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

  private final ApiError error;

  public ApiException(String message, Throwable cause, ApiError error) {
    super(message, cause);
    this.error = error;
  }

  public ApiException(String message, ApiError error) {
    super(message);
    this.error = error;
  }
}
