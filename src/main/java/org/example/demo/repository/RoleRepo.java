package org.example.demo.repository;

import java.util.Optional;
import org.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

  Optional<Role> findByRoleName(String roleName);
}
