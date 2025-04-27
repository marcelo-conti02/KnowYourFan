package com.furia.know_your_fan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.furia.know_your_fan.entity.UserActivity;
import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserIdOrderByActivityDateDesc(Long userId);
}
