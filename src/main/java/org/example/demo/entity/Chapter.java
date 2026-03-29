package org.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chapterNumber;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content; // The actual novel text

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;
}
