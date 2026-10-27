package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Minute;
import com.meetingminutes.meeting_minutes_backend.service.MinuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/minutes")
@RequiredArgsConstructor
public class MinuteController {

    private final MinuteService service;

    @GetMapping
    public List<Minute> getAll(@RequestParam(required = false) Integer meetingId) {
        return meetingId == null ? service.findAll() : service.findByMeeting(meetingId);
    }

    @GetMapping("/{id}")
    public Minute getById(@PathVariable Integer id) {
        return service.findById(id);
    }

   @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Minute create(@RequestBody Minute minute, Authentication authentication) {
    return service.create(minute, authentication.getName());
}

    @PutMapping("/{id}")
    public Minute update(@PathVariable Integer id, @RequestBody Minute minute) {
        return service.update(id, minute);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}