package org.ekipa.pnes.api.services;


import lombok.RequiredArgsConstructor;
import org.ekipa.pnes.api.models.Role;
import org.ekipa.pnes.api.models.User;
import org.ekipa.pnes.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public User findById(Integer id) {
        Optional<User> optional = userRepository.findById(id);
        return optional.orElse(null);
    }

    public void save(User user) throws EntityIntegrityException {
        if (checkEntityIntegrity(user)) {
            userRepository.save(user);
        }
    }

    private boolean checkEntityIntegrity(User user) throws EntityIntegrityException {
        try {
            boolean success = true;
            if (user.getRoles().size() < 0) throw new EntityIntegrityException("User cannot exist without roles");
            for (Role r : user.getRoles()) {
                if (r.getUsers().stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) {
                    success = false;
                    r.addUser(user);
                }
            }
            return success || checkEntityIntegrity(user);

        } catch (Exception ex) {
            throw new EntityIntegrityException(ex.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}