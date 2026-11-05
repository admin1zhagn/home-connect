package com.homeconnect.controller;

import com.homeconnect.common.R;
import com.homeconnect.dto.AuthRequest;
import com.homeconnect.dto.AuthResponse;
import com.homeconnect.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<AuthResponse> login(@RequestBody AuthRequest req) {
        return R.ok(authService.login(req.getUsername(), req.getPassword()));
    }
}
