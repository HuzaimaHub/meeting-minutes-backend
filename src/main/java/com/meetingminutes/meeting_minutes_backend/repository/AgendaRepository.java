package com.meetingminutes.meeting_minutes_backend.repository;

import com.meetingminutes.meeting_minutes_backend.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {
    List<Agenda> findByMeeting_MeetingIdOrderByAgendaOrderAsc(Integer meetingId);
}