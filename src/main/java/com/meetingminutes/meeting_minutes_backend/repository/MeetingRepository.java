package com.meetingminutes.meeting_minutes_backend.repository;

import com.meetingminutes.meeting_minutes_backend.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    List<Meeting> findByTitleContainingIgnoreCase(String title);

    List<Meeting> findByMeetingDateGreaterThanEqualOrderByMeetingDateAscStartTimeAsc(LocalDate date);

    @Query("""
            select m from Meeting m
            where m.meetingDate < :today
              and m.meetingDate >= :fromDate
              and m.meetingDate <= :toDate
              and lower(m.title) like lower(concat('%', :q, '%'))
            """)
    Page<Meeting> searchPast(@Param("today") LocalDate today,
                             @Param("q") String q,
                             @Param("fromDate") LocalDate fromDate,
                             @Param("toDate") LocalDate toDate,
                             Pageable pageable);
}