package com.example.demo.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

    public static final String PUBLIC_PREFIX = "/uploads/";
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    private final Path root;

    public ImageStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de imágenes: " + root, e);
        }
    }

    public Path root() {
        return root;
    }

    public Optional<String> validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        if (file.getSize() > MAX_BYTES) {
            return Optional.of("La imagen supera los 5 MB");
        }
        try {
            if (detectExtension(file.getBytes()) == null) {
                return Optional.of("Formato no válido. Usa PNG, JPG, WEBP o GIF");
            }
        } catch (IOException e) {
            return Optional.of("No se pudo leer la imagen");
        }
        return Optional.empty();
    }


    public Optional<String> store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        try {
            byte[] bytes = file.getBytes();
            String extension = detectExtension(bytes);
            if (extension == null || bytes.length > MAX_BYTES) {
                throw new IllegalArgumentException("Imagen no válida");
            }
            String filename = UUID.randomUUID() + "." + extension;
            Files.write(root.resolve(filename), bytes);
            return Optional.of(PUBLIC_PREFIX + filename);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }
    }

    public void delete(String url) {
        if (url == null || !url.startsWith(PUBLIC_PREFIX)) {
            return;
        }
        Path file = root.resolve(url.substring(PUBLIC_PREFIX.length())).normalize();
        if (!file.startsWith(root)) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {

        }
    }


    private static String detectExtension(byte[] b) {
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "png";
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') {
            return "gif";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }
}