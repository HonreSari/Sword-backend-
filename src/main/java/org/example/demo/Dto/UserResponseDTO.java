package org.example.demo.Dto;

import java.util.Set;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    Boolean isVip,
    Integer creditBalance,
    Set<String> roles // Just role names, not entities
) {
}
