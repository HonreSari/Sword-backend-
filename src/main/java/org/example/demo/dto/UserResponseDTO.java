package org.example.demo.Dto;

import org.example.demo.entity.User;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    Boolean isVip,
    Integer creditBalance,
    Set<String> roles) {
  public static UserResponseDTO fromEntity(User user) {
    return new UserResponseDTO(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.isVip(),
        user.getCreditBalance(),
        user.getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toSet()));
  }
}
