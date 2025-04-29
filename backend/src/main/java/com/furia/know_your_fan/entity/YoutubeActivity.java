package com.furia.know_your_fan.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // ex: "like", "comment", "upload"
    private String videoId;
    private String title;
    private String description;
    private Instant publishedAt;

    @ManyToOne
    @JoinColumn(name = "fan_profile_id")
    private FanProfile fanProfile;
}
