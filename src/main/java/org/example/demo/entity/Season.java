package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seasonName; // "Season 1", "Heavenly Realm Arc"
    private Integer seasonOrder;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();


    public void addEpisode(Episode episode) {
        episodes.add(episode);
        episode.setSeason(this);
    }
}
