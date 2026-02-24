package com.ey.app.controller;

import com.ey.app.controller.dto.request.RegisterUserRequest;
import com.ey.app.controller.dto.response.RegisterUserResponse;
import com.ey.app.service.business.AuthBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController implements AuthResource {

    private final AuthBusinessService authBusinessService;

    public AuthController(AuthBusinessService authBusinessService) {
        this.authBusinessService = authBusinessService;
    }

    @Override
    public ResponseEntity<RegisterUserResponse> registerUser(RegisterUserRequest request) {
        return authBusinessService.registerUser(request);
    }
}
