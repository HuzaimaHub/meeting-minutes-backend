package com.meetingminutes.meeting_minutes_backend.controller;

import com.meetingminutes.meeting_minutes_backend.entity.Meeting;
import com.meetingminutes.meeting_minutes_backend.repository.AgendaRepository;
import com.meetingminutes.meeting_minutes_backend.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final MeetingRepository meetingRepository;
    private final AgendaRepository agendaRepository;

    public record PublicAgenda(Integer agendaOrder, String topic, String description) {}

    public record PublicMeeting(Integer meetingId, String title, String purpose,
                                LocalDate meetingDate, LocalTime startTime,
                                LocalTime endTime, String location,
                                List<PublicAgenda> agenda) {}

    public record PastPage(List<PublicMeeting> items, long total,
                           int page, int size, int totalPages) {}

    @GetMapping("/meetings")
    public List<PublicMeeting> upcomingMeetings() {
        return meetingRepository
                .findByMeetingDateGreaterThanEqualOrderByMeetingDateAscStartTimeAsc(LocalDate.now())
                .stream()
                .map(this::toPublic)
                .toList();
    }

    @GetMapping("/past-meetings")
    public PastPage pastMeetings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String term = q == null ? "" : q.trim();
        LocalDate fromDate = from != null ? from : LocalDate.of(1900, 1, 1);
        LocalDate toDate = to != null ? to : LocalDate.of(2999, 12, 31);

        Page<Meeting> result = meetingRepository.searchPast(
                LocalDate.now(), term, fromDate, toDate,
                PageRequest.of(safePage, safeSize,
                        Sort.by(Sort.Order.desc("meetingDate"), Sort.Order.desc("startTime"))));

        return new PastPage(
                result.getContent().stream().map(this::toPublic).toList(),
                result.getTotalElements(), safePage, safeSize, result.getTotalPages());
    }

    private PublicMeeting toPublic(Meeting m) {
        List<PublicAgenda> agenda = agendaRepository
                .findByMeeting_MeetingIdOrderByAgendaOrderAsc(m.getMeetingId())
                .stream()
                .map(a -> new PublicAgenda(a.getAgendaOrder(), a.getTopic(), a.getDescription()))
                .toList();
        return new PublicMeeting(m.getMeetingId(), m.getTitle(), m.getPurpose(),
                m.getMeetingDate(), m.getStartTime(), m.getEndTime(), m.getLocation(), agenda);
    }
}