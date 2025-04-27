package com.furia.know_your_fan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.furia.know_your_fan.entity.FanProfile;
import com.furia.know_your_fan.repository.FanProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FanProfileService {

    private final FanProfileRepository fanProfileRepository;

    public FanProfile save(FanProfile fanProfile) {
        return fanProfileRepository.save(fanProfile);
    }

    public FanProfile findByUserId(Long userId) {
        return fanProfileRepository.findByUserId(userId);
    }
}
