package com.meetingminutes.meeting_minutes_backend.config;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner ensureDefaultAdmin(AdminRepository repo, PasswordEncoder encoder) {
        return args -> {
            Admin admin = repo.findByEmail("admin@meeting.com").orElseGet(() -> {
                Admin a = new Admin();
                a.setFullName("System Admin");
                a.setEmail("admin@meeting.com");
                a.setGender("Male");
                a.setRole("ADMIN");
                return a;
            });
            if (admin.getPassword() == null || !admin.getPassword().startsWith("$2")) {
                admin.setPassword(encoder.encode("Admin@123"));
                repo.save(admin);
            }
        };
    }
}