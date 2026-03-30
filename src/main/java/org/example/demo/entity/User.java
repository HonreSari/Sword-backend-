package org.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false)
  private String password; // hashed password

  @Column(name = "credit_balance", nullable = false, precision = 10, scale = 2)
  private BigDecimal creditBalance = BigDecimal.ZERO;

  // Current active VIP Tier (Many-to-One)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "active_vip_tier_id")
  private VipTier activeVipTier;

  @Column(name = "vip_expiry_date")
  private LocalDateTime vipExpiryDate;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at")
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  // Helper method
  public boolean hasActiveVip() {
    return activeVipTier != null
        && (vipExpiryDate == null || vipExpiryDate.isAfter(LocalDateTime.now()));
  }
}
