package com.meetingminutes.meeting_minutes_backend.service;

import com.meetingminutes.meeting_minutes_backend.entity.Admin;
import com.meetingminutes.meeting_minutes_backend.entity.PasswordResetToken;
import com.meetingminutes.meeting_minutes_backend.repository.AdminRepository;
import com.meetingminutes.meeting_minutes_backend.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration VALIDITY = Duration.ofMinutes(30);
    private static final Duration THROTTLE = Duration.ofSeconds(60);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BrandStorageService brand;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /** Always finishes silently, so callers cannot learn whether an email exists. */
    @Transactional
    public void requestReset(String emailRaw) {
        String email = emailRaw == null ? "" : emailRaw.trim();
        if (email.isEmpty()) {
            return;
        }
        Optional<Admin> found = adminRepository.findByEmail(email);
        if (found.isEmpty()) {
            return;
        }
        Admin admin = found.get();
        LocalDateTime now = LocalDateTime.now();

        // At most one new link per minute for the same account
        Optional<PasswordResetToken> last =
                tokenRepository.findFirstByAdmin_AdminIdOrderByCreatedAtDesc(admin.getAdminId());
        if (last.isPresent() && last.get().getCreatedAt() != null
                && last.get().getCreatedAt().isAfter(now.minus(THROTTLE))) {
            return;
        }

        tokenRepository.deleteByAdmin_AdminId(admin.getAdminId());

        String raw = newToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setAdmin(admin);
        token.setTokenHash(sha256(raw));
        token.setExpiresAt(now.plus(VALIDITY));
        tokenRepository.save(token);

        String link = frontendUrl.replaceAll("/+$", "") + "/reset-password?token=" + raw;
        BrandStorageService.Org org = brand.getOrg();
        mailService.sendResetLink(admin.getEmail(), admin.getFullName(), link,
                brand.getSystemName(), org.name());
    }

    @Transactional(readOnly = true)
    public void validateToken(String raw) {
        find(raw);
    }

    @Transactional
    public void resetPassword(String raw, String newPassword) {
        PasswordResetToken token = find(raw);
        PasswordPolicy.check(newPassword);

        Admin admin = token.getAdmin();
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);

        // The link can be used only once
        tokenRepository.deleteByAdmin_AdminId(admin.getAdminId());
    }

    private PasswordResetToken find(String raw) {
        if (raw == null || raw.isBlank()) {
            throw invalid();
        }
        PasswordResetToken token = tokenRepository.findByTokenHash(sha256(raw.trim()))
                .orElseThrow(PasswordResetService::invalid);
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw invalid();
        }
        return token;
    }

    private static ResponseStatusException invalid() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "This reset link is invalid or has expired. Request a new one.");
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}