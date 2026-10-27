package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Agenda;
import com.meetingminutes.meeting_minutes_backend.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository repository;

    public List<Agenda> findAll() {
        return repository.findAll();
    }

    public List<Agenda> findByMeeting(Integer meetingId) {
        return repository.findByMeeting_MeetingIdOrderByAgendaOrderAsc(meetingId);
    }

    public Agenda findById(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Agenda not found"));
    }

    public Agenda create(Agenda agenda) {
        agenda.setAgendaId(null);
        return repository.save(agenda);
    }

    public Agenda update(Integer id, Agenda input) {
        Agenda agenda = findById(id);
        agenda.setTopic(input.getTopic());
        agenda.setDescription(input.getDescription());
        agenda.setAgendaOrder(input.getAgendaOrder());
        return repository.save(agenda);
    }

    public void delete(Integer id) {
        findById(id);
        repository.deleteById(id);
    }
}