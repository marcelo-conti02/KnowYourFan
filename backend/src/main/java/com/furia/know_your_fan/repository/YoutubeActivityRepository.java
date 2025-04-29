package com.furia.know_your_fan.repository;

import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.entity.YoutubeActivity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YoutubeActivityRepository extends JpaRepository<YoutubeActivity, Long>{
     List<YoutubeActivity> findByFanProfile(FanProfile fanProfile);
}
