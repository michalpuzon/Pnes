package org.ekipa.pnes.api.services;

import lombok.RequiredArgsConstructor;
import org.ekipa.pnes.api.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public boolean authenticate(String username, String password) {
        UserDetails fromDB = userService.loadUserByUsername(username);
        if (fromDB == null) return false;
        if (!fromDB.getUsername().equals(username)) return false;
        if (!passwordEncoder.matches(password, fromDB.getPassword())) return false;
        return true;
    }

    public boolean register(User user) throws EntityIntegrityException {
        if (userService.loadUserByUsername(user.getUsername()) != null) return false;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return true;
    }
}
