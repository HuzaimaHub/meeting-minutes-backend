package com.meetingminutes.meeting_minutes_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Attendee")
@Getter @Setter
public class Attendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttendeeID")
    private Integer attendeeId;

    @ManyToOne
    @JoinColumn(name = "MeetingID", nullable = false)
    private Meeting meeting;

    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "Comments")
    private String comments;

    @Column(name = "Institution")
    private String institution;

    @Column(name = "Position")
    private String position;
}