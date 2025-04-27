package com.furia.know_your_fan.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furia.know_your_fan.entity.UserActivity;
import com.furia.know_your_fan.repository.UserActivityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivity save(UserActivity activity) {
        return userActivityRepository.save(activity);
    }

    public List<UserActivity> findByUserId(Long userId) {
        return userActivityRepository.findByUserIdOrderByActivityDateDesc(userId);
    }
}
