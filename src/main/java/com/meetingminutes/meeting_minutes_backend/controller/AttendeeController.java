package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Attendee;
import com.meetingminutes.meeting_minutes_backend.service.AttendeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendees")
@RequiredArgsConstructor
public class AttendeeController {

    private final AttendeeService service;

    @GetMapping
    public List<Attendee> getAll(@RequestParam(required = false) Integer meetingId) {
        return meetingId == null ? service.findAll() : service.findByMeeting(meetingId);
    }

    @GetMapping("/{id}")
    public Attendee getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendee create(@RequestBody Attendee attendee) {
        return service.create(attendee);
    }

    @PutMapping("/{id}")
    public Attendee update(@PathVariable Integer id, @RequestBody Attendee attendee) {
        return service.update(id, attendee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}