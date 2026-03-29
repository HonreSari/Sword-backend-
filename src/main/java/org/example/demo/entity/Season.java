package org.example.demo.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seasonName; // "Season 1", "Heavenly Realm Arc"
    private Integer seasonOrder;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @OneToMany(mappedBy = "season")
    private List<Episode> episodes;
}
