package com.furia.know_your_fan.repository;

import com.furia.know_your_fan.entity.YoutubeSubscription;
import com.furia.know_your_fan.entity.FanProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YoutubeSubscriptionRepository extends JpaRepository<YoutubeSubscription, Long> {
    List<YoutubeSubscription> findByFanProfile(FanProfile fanProfile);
    void deleteByFanProfile(FanProfile fanProfile);
}
