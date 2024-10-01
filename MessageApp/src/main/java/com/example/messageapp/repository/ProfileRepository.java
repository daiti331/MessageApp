package com.example.messageapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.messageapp.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUserId(Long userId);
}