package org.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "episodes")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer episodeNumber;
    private String title;
    private String videoUrl; // Link to your storage (S3/Cloudinary)
    private String duration;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;
}
