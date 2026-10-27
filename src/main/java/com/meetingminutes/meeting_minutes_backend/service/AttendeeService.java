package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Attendee;
import com.meetingminutes.meeting_minutes_backend.repository.AttendeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository repository;

    public List<Attendee> findAll() {
        return repository.findAll();
    }

    public List<Attendee> findByMeeting(Integer meetingId) {
        return repository.findByMeeting_MeetingId(meetingId);
    }

    public Attendee findById(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendee not found"));
    }

    public Attendee create(Attendee attendee) {
        attendee.setAttendeeId(null);
        return repository.save(attendee);
    }

    public Attendee update(Integer id, Attendee input) {
        Attendee attendee = findById(id);
        attendee.setFullName(input.getFullName());
        attendee.setComments(input.getComments());
        attendee.setInstitution(input.getInstitution());
        attendee.setPosition(input.getPosition());
        return repository.save(attendee);
    }

    public void delete(Integer id) {
        findById(id);
        repository.deleteById(id);
    }
}