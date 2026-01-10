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
import org.itmo.isLab1.users.Role;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/artist/sign-up")
    public AuthenticationDto signUpArtist(@RequestBody @Valid SignUpDto request) {
        return authenticationService.signUp(request, Role.ROLE_ARTIST);
    }

    @PostMapping("/expert/sign-up")
    public AuthenticationDto signUpExpert(@RequestBody @Valid SignUpDto request) {
        return authenticationService.signUp(request, Role.ROLE_EXPERT);
    }

    @PostMapping("/residence-admin/sign-up")
    public AuthenticationDto signUpResidenceAdmin(@RequestBody @Valid SignUpDto request) {
        return authenticationService.signUp(request, Role.ROLE_RESIDENCE_ADMIN);
    }

    @PostMapping("/sign-in")
    public AuthenticationDto signIn(@RequestBody @Valid SignInDto request) {
        return authenticationService.signIn(request);
    }
}