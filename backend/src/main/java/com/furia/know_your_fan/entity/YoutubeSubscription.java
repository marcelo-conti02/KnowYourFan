package com.furia.know_your_fan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelId;
    private String channelTitle;
    private String channelThumbnailUrl;

    @ManyToOne
    @JoinColumn(name = "fan_profile_id")
    private FanProfile fanProfile;
}
