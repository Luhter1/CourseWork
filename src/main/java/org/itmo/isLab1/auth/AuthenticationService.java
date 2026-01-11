package org.itmo.isLab1.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.itmo.isLab1.auth.dto.AuthenticationDto;
import org.itmo.isLab1.auth.dto.SignInDto;
import org.itmo.isLab1.auth.dto.SignUpDto;
import org.itmo.isLab1.users.Role;
import org.itmo.isLab1.users.User;
import org.itmo.isLab1.users.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     */
    @Transactional
    public AuthenticationDto signUp(SignUpDto request) {
        Role role = request.getRole() == Role.ROLE_SUPERADMIN ? Role.ROLE_ARTIST : request.getRole();
        var user = User.builder()
            .username(request.getEmail())
            .name(request.getName())
            .surname(request.getSurname())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .is_active(true)
            .build();
        userService.create_User(user, role);

        var jwt = jwtService.generateToken(user);
        return new AuthenticationDto(jwt, user);
    }

    /**
     * Аутентификация пользователя
     */
    @Transactional
    public AuthenticationDto signIn(SignInDto request) {
        var user = userService.getByUsername(request.getEmail());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));

        var jwt = jwtService.generateToken(user);
        return new AuthenticationDto(jwt, user);
    }
}
