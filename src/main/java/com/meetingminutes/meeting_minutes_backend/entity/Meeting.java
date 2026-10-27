package com.meetingminutes.meeting_minutes_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Meetings")
@Getter @Setter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MeetingID")
    private Integer meetingId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Purpose")
    private String purpose;

    @Column(name = "MeetingDate", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime")
    private LocalTime endTime;

    @Column(name = "Location")
    private String location;

    @Column(name = "ShowAgendaPublic", nullable = false)
    private Boolean showAgendaPublic = true;

    @ManyToOne
    @JoinColumn(name = "CreatedBy", nullable = false)
    private Admin createdBy;
}