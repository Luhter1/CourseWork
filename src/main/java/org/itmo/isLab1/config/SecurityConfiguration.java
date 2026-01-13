package org.itmo.isLab1.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.itmo.isLab1.auth.JwtAuthenticationFilter;
import org.itmo.isLab1.utils.crypto.Sha512PasswordEncoder;
import org.itmo.isLab1.users.UserService;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    @Order(1)
    public SecurityFilterChain websocketSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/ws/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of(
                    "http://localhost:3000",
                    "http://localhost:5000",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:16123",
                    "http://localhost:16123"
                ));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setMaxAge(10L);
                corsConfiguration.addExposedHeader("X-Response-Uuid");
                corsConfiguration.addExposedHeader("X-Total-Count");
                return corsConfiguration;
            }))
            .authorizeHttpRequests(request -> {
                // Аутентификация
                request.requestMatchers("/api/auth/**").permitAll();

                // Публичный доступ к просмотру artists (GET /{id}/achievements)
                request.requestMatchers(HttpMethod.GET, "/api/artists/**").permitAll();

                // Операции с /me только для ARTIST
                request.requestMatchers(HttpMethod.POST, "/api/artists/me/**").hasRole("ARTIST")
                      .requestMatchers(HttpMethod.PUT, "/api/artists/me/**").hasRole("ARTIST")
                      .requestMatchers(HttpMethod.DELETE, "/api/artists/me/**").hasRole("ARTIST")
                      .requestMatchers(HttpMethod.GET, "/api/artists/me/**").hasRole("ARTIST");

                // Публичный доступ к просмотру residences (проверка публикации в сервисе)
                request.requestMatchers(HttpMethod.GET, "/api/residences/**").permitAll();

                // Операции с /me только для RESIDENCE_ADMIN
                request.requestMatchers("/api/residences/me/**").hasRole("RESIDENCE_ADMIN");

                // Всё остальное - deny
                request.anyRequest().denyAll();
            })
            .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha512PasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}