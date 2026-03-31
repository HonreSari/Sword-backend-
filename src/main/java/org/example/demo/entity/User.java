package org.example.demo.entity;

import jakarta.persistence.*;
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

  // ✅ KEEP (Optional): Simple credit system for demo
  // Change BigDecimal → Integer for simplicity unless you need decimals
  @Column(name = "credit_balance", nullable = false)
  private Integer creditBalance = 0;

  // ❌ REMOVE: Complex VIP relationship
  // @ManyToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "active_vip_tier_id")
  // private VipTier activeVipTier;

  // ❌ REMOVE: Expiry logic (too complex for MVP)
  // @Column(name = "vip_expiry_date")
  // private LocalDateTime vipExpiryDate;

  // ✅ ADD: Simple VIP flag (replaces complex tier logic)
  @Column(name = "is_vip", nullable = false)
  private boolean isVip = false;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at")
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  // ✅ UPDATED: Simple VIP check (no expiry logic needed for MVP)
  public boolean hasActiveVip() {
    return this.isVip;
  }

  // ✅ HELPER: Add credits (simplified)
  public void addCredits(int amount) {
    this.creditBalance += amount;
  }

  // ✅ HELPER: Spend credits
  public boolean spendCredits(int amount) {
    if (this.creditBalance >= amount) {
      this.creditBalance -= amount;
      return true;
    }
    return false;
  }

  // === Getters & Setters ===
  // (Use Lombok @Data or generate via IDE)
}
