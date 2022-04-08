package org.ekipa.pnes.api.services;

import lombok.RequiredArgsConstructor;
import org.ekipa.pnes.api.models.Role;
import org.ekipa.pnes.api.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findById(Integer id) {
        Optional<Role> optional = roleRepository.findById(id);
        return optional.orElse(null);
    }

    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public Role getRoleByTitle(String title) {
        return roleRepository.getRoleByTitle(title);
    }
}