package com.meetingminutes.meeting_minutes_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Agenda")
@Getter @Setter
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AgendaID")
    private Integer agendaId;

    @ManyToOne
    @JoinColumn(name = "MeetingID", nullable = false)
    private Meeting meeting;

    @Column(name = "Topic", nullable = false)
    private String topic;

    @Column(name = "Description")
    private String description;

    @Column(name = "AgendaOrder", nullable = false)
    private Integer agendaOrder;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;
}