package com.furia.know_your_fan.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FanProfile extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    private String favoriteGame;

    @ElementCollection
    private List<String> favoriteFuriaPlayers;

    @Min(0) 
    @Max(100)
    private Integer engagementScore;

    @PastOrPresent
    private LocalDateTime lastInteractionDate;

    @ElementCollection
    private Map<String, String> platformUsernames; // example: {"steam": "username", "twitch": "username"}
}
