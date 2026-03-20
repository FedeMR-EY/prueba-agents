package com.ey.app.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public record ApiError(String type, String title, Map<String, String> detail, HttpStatus status) {}
