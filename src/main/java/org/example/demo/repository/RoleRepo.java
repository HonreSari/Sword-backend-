package org.example.demo.repository;

import java.util.Optional;

import org.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {

  Optional<Role> findByName(String roleName);

}
