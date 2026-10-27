package com.meetingminutes.meeting_minutes_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class BrandStorageService {

    public static final String DEFAULT_SYSTEM_NAME = "Meeting Minutes Management System";
    public static final String DEFAULT_ORG_NAME = "Zanzibar Social Security Fund";
    public static final String DEFAULT_ORG_NAME_SW = "Mfuko wa Hifadhi ya Jamii Zanzibar";
    public static final String DEFAULT_ORG_SHORT = "ZSSF";

    private static final List<String> EXTENSIONS = List.of("png", "jpg", "webp");
    private static final long MAX_BYTES = 2L * 1024 * 1024;

    public record Org(String name, String nameSw, String shortName) {}

    private final Path dir;

    public BrandStorageService(@Value("${app.storage.dir:./data}") String base) throws IOException {
        this.dir = Path.of(base, "brand").toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    // ---------- Logo ----------

    public Optional<Path> findLogo() {
        for (String ext : EXTENSIONS) {
            Path p = dir.resolve("logo." + ext);
            if (Files.isRegularFile(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public void saveLogo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw bad("Choose an image file.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw bad("The image must be 2 MB or smaller.");
        }
        byte[] bytes = file.getBytes();
        String ext = detectExtension(bytes);
        if (ext == null) {
            throw bad("Only PNG, JPG or WEBP images are allowed.");
        }
        deleteLogo();
        Files.write(dir.resolve("logo." + ext), bytes);
    }

    public void deleteLogo() throws IOException {
        for (String ext : EXTENSIONS) {
            Files.deleteIfExists(dir.resolve("logo." + ext));
        }
    }

    public String contentType(Path file) {
        String name = file.getFileName().toString();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    // ---------- System name ----------

    public String getSystemName() {
        String name = readText("system-name.txt", DEFAULT_SYSTEM_NAME);
        return name.isEmpty() ? DEFAULT_SYSTEM_NAME : name;
    }

    public void saveSystemName(String raw) throws IOException {
        String name = clean(raw);
        if (name.length() < 3 || name.length() > 80) {
            throw bad("The system name must be between 3 and 80 characters.");
        }
        writeText("system-name.txt", name);
    }

    public void resetSystemName() throws IOException {
        Files.deleteIfExists(dir.resolve("system-name.txt"));
    }

    // ---------- Organization ----------

    public Org getOrg() {
        String name = readText("org-name.txt", DEFAULT_ORG_NAME);
        String nameSw = readText("org-name-sw.txt", DEFAULT_ORG_NAME_SW);
        String shortName = readText("org-short.txt", DEFAULT_ORG_SHORT);
        return new Org(
                name.isEmpty() ? DEFAULT_ORG_NAME : name,
                nameSw,
                shortName.isEmpty() ? DEFAULT_ORG_SHORT : shortName);
    }

    public void saveOrg(String name, String nameSw, String shortName) throws IOException {
        String n = clean(name);
        String sw = clean(nameSw);
        String sh = clean(shortName);
        if (n.length() < 3 || n.length() > 80) {
            throw bad("The organization name must be between 3 and 80 characters.");
        }
        if (sw.length() > 80) {
            throw bad("The Swahili name must be 80 characters or fewer.");
        }
        if (sh.length() < 2 || sh.length() > 12) {
            throw bad("The short name must be between 2 and 12 characters.");
        }
        writeText("org-name.txt", n);
        writeText("org-name-sw.txt", sw);
        writeText("org-short.txt", sh);
    }

    public void resetOrg() throws IOException {
        Files.deleteIfExists(dir.resolve("org-name.txt"));
        Files.deleteIfExists(dir.resolve("org-name-sw.txt"));
        Files.deleteIfExists(dir.resolve("org-short.txt"));
    }

    // ---------- Helpers ----------

    private String readText(String file, String fallback) {
        Path p = dir.resolve(file);
        try {
            if (Files.isRegularFile(p)) {
                return Files.readString(p, StandardCharsets.UTF_8).trim();
            }
        } catch (IOException ignored) {
            // fall back to the default value
        }
        return fallback;
    }

    private void writeText(String file, String value) throws IOException {
        Files.writeString(dir.resolve(file), value, StandardCharsets.UTF_8);
    }

    private String clean(String raw) {
        return raw == null ? "" : raw.trim().replaceAll("\\s+", " ");
    }

    private String detectExtension(byte[] b) {
        if (b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "png";
        }
        if (b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}