package com.meetingminutes.meeting_minutes_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Minutes")
@Getter @Setter
public class Minute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MinuteID")
    private Integer minuteId;

    @ManyToOne
    @JoinColumn(name = "MeetingID", nullable = false)
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "AgendaID", nullable = false)
    private Agenda agenda;

    @Column(name = "Discussion", columnDefinition = "NVARCHAR(MAX)")
    private String discussion;

    @Column(name = "Decision", columnDefinition = "NVARCHAR(MAX)")
    private String decision;

    @ManyToOne
    @JoinColumn(name = "RecordedBy", nullable = false)
    private Admin recordedBy;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;
}