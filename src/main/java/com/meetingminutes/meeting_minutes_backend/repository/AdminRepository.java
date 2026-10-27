package com.meetingminutes.meeting_minutes_backend.repository;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByEmail(String email);
}