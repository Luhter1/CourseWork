package org.itmo.isLab1.users;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.itmo.isLab1.common.errors.UserWithThisUsernameAlreadyExists;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    public User create_User(User user, Role role) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new UserWithThisUsernameAlreadyExists("Пользователь с таким именем уже существует");
        }

        if (repository.count() == 0) {
            logger.info("Creating first user with SUPERADMIN role");
            user.setRole(Role.ROLE_SUPERADMIN);
        } else {
            user.setRole(role);
        }
        return save(user);
    }

    // Получение пользователя по имени пользователя
    public User getByUsername(String username) {
        return repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    // Получение пользователя по имени пользователя
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    // Получение текущего пользователя
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Получение текущего пользователя из контекста Spring Security
    public User getCurrentUser() {
        var username = getCurrentUsername();
        return getByUsername(username);
    }
}

