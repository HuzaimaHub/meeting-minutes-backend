package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Agenda;
import com.meetingminutes.meeting_minutes_backend.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService service;

    @GetMapping
    public List<Agenda> getAll(@RequestParam(required = false) Integer meetingId) {
        return meetingId == null ? service.findAll() : service.findByMeeting(meetingId);
    }

    @GetMapping("/{id}")
    public Agenda getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agenda create(@RequestBody Agenda agenda) {
        return service.create(agenda);
    }

    @PutMapping("/{id}")
    public Agenda update(@PathVariable Integer id, @RequestBody Agenda agenda) {
        return service.update(id, agenda);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}