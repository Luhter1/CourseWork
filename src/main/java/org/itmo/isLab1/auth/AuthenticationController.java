package org.itmo.isLab1.auth;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.itmo.isLab1.auth.dto.AuthenticationDto;
import org.itmo.isLab1.auth.dto.SignInDto;
import org.itmo.isLab1.auth.dto.SignUpDto;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthenticationDto signUpArtist(@RequestBody @Valid SignUpDto request) {
        return authenticationService.signUp(request);
    }

    @PostMapping("/login")
    public AuthenticationDto signIn(@RequestBody @Valid SignInDto request) {
        return authenticationService.signIn(request);
    }
}