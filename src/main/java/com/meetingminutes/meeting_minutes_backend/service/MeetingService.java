package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.entity.Meeting;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import com.meetingminutes.meeting_minutes_backend.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository repository;
    private final AdminRepository adminRepository;

    public List<Meeting> findAll() {
        return repository.findAll(Sort.by("meetingDate").descending());
    }

    public Meeting findById(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));
    }

    public List<Meeting> search(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public Meeting create(Meeting meeting, String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        meeting.setMeetingId(null);
        meeting.setCreatedBy(admin);
        meeting.setShowAgendaPublic(Boolean.TRUE.equals(meeting.getShowAgendaPublic()));
        return repository.save(meeting);
    }

    public Meeting update(Integer id, Meeting input) {
        Meeting meeting = findById(id);
        meeting.setTitle(input.getTitle());
        meeting.setPurpose(input.getPurpose());
        meeting.setMeetingDate(input.getMeetingDate());
        meeting.setStartTime(input.getStartTime());
        meeting.setEndTime(input.getEndTime());
        meeting.setLocation(input.getLocation());
        meeting.setShowAgendaPublic(Boolean.TRUE.equals(input.getShowAgendaPublic()));
        return repository.save(meeting);
    }

    public void delete(Integer id) {
        findById(id);
        repository.deleteById(id);
    }
}