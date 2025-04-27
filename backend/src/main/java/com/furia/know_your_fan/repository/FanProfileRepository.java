package com.furia.know_your_fan.repository;

import com.furia.know_your_fan.entity.FanProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FanProfileRepository extends JpaRepository<FanProfile, Long> {
    FanProfile findByUserId(Long userId);
}
