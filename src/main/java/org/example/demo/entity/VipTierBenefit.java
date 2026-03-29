package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vip_tier_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VipTierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vip_tier_id", nullable = false)
    private VipTier vipTier;

    @Column(nullable = false, length = 255)
    private String benefit;
}