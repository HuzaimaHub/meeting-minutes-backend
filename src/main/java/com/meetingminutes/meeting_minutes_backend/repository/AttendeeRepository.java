package com.meetingminutes.meeting_minutes_backend.repository;

import com.meetingminutes.meeting_minutes_backend.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    List<Attendee> findByMeeting_MeetingId(Integer meetingId);
}