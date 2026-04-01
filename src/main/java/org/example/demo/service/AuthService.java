package org.example.demo.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.demo.Dto.LoginRequest;
import org.example.demo.Dto.RegisterRequest;
import org.example.demo.Dto.UserResponseDTO;
import org.example.demo.entity.User;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.repository.UserRepository;
import org.example.demo.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public Map<String, Object> register(RegisterRequest request) {
    // Check if user exists
    if (userRepository.findByUsername(request.username()).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }

    User user = new User();
    user.setUsername(request.username());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setVip(false);
    user.setCreditBalance(0);

    userRepository.save(user);

    String token = jwtUtil.generateToken(user.getUsername());
    return Map.of("token", token, "user", UserResponseDTO.fromEntity(user));
  }

  public Map<String, Object> login(LoginRequest request) {
    User user =
        userRepository
            .findByUsername(request.username())
            .orElseThrow(
                () -> new ResourceNotFoundException("User", "username", request.username()));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }

    String token = jwtUtil.generateToken(user.getUsername());
    return Map.of("token", token, "user", UserResponseDTO.fromEntity(user));
  }
}
