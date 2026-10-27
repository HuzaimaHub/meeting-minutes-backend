package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.entity.Agenda;
import com.meetingminutes.meeting_minutes_backend.entity.Minute;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import com.meetingminutes.meeting_minutes_backend.repository.AgendaRepository;
import com.meetingminutes.meeting_minutes_backend.repository.MinuteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinuteService {

    private final MinuteRepository repository;
    private final AdminRepository adminRepository;
    private final AgendaRepository agendaRepository;

    public List<Minute> findAll() {
        return repository.findAll();
    }

    public List<Minute> findByMeeting(Integer meetingId) {
        return repository.findByMeeting_MeetingId(meetingId);
    }

    public Minute findById(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Minute not found"));
    }

    public Minute create(Minute minute, String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        Agenda agenda = loadAgenda(minute);
        minute.setMinuteId(null);
        minute.setAgenda(agenda);
        minute.setMeeting(agenda.getMeeting());
        minute.setRecordedBy(admin);
        return repository.save(minute);
    }

    public Minute update(Integer id, Minute input) {
        Minute minute = findById(id);
        minute.setDiscussion(input.getDiscussion());
        minute.setDecision(input.getDecision());
        if (input.getAgenda() != null && input.getAgenda().getAgendaId() != null) {
            Agenda agenda = loadAgenda(input);
            if (!agenda.getMeeting().getMeetingId().equals(minute.getMeeting().getMeetingId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Agenda belongs to a different meeting");
            }
            minute.setAgenda(agenda);
        }
        return repository.save(minute);
    }

    public void delete(Integer id) {
        findById(id);
        repository.deleteById(id);
    }

    private Agenda loadAgenda(Minute minute) {
        if (minute.getAgenda() == null || minute.getAgenda().getAgendaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Agenda is required");
        }
        return agendaRepository.findById(minute.getAgenda().getAgendaId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda not found"));
    }
}