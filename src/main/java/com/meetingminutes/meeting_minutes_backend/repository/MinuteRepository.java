package com.meetingminutes.meeting_minutes_backend.repository;

import com.meetingminutes.meeting_minutes_backend.entity.Minute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MinuteRepository extends JpaRepository<Minute, Integer> {
    List<Minute> findByMeeting_MeetingId(Integer meetingId);
}