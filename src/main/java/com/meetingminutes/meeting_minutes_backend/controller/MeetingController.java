package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Meeting;
import com.meetingminutes.meeting_minutes_backend.service.MeetingService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService service;

    @GetMapping
    public List<Meeting> getAll() {
        return service.findAll();
    }

    @GetMapping("/search")
    public List<Meeting> search(@RequestParam String title) {
        return service.search(title);
    }

    @GetMapping("/{id}")
    public Meeting getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Meeting create(@RequestBody Meeting meeting, Authentication authentication) {
    return service.create(meeting, authentication.getName());
}

    @PutMapping("/{id}")
    public Meeting update(@PathVariable Integer id, @RequestBody Meeting meeting) {
        return service.update(id, meeting);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}