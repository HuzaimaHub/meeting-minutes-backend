package com.meetingminutes.meeting_minutes_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MeetingMinutesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingMinutesBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner showLinks(@Value("${server.port:8080}") int port) {
        return args -> {
            System.out.println();
            System.out.println("Server is ready. Open in browser:");
            System.out.println("  http://localhost:" + port + "/api/meetings");
            System.out.println("  http://localhost:" + port + "/api/agendas");
System.out.println("  http://localhost:" + port + "/api/attendees");
System.out.println("  http://localhost:" + port + "/api/minutes"); 
            System.out.println();
        };
    }
}