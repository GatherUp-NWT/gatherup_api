package org.app.eventservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket.name}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadImage(MultipartFile file, UUID eventId) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = "event_" + eventId + "_" + System.currentTimeMillis() + fileExtension;

            // Create the upload URL
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            // Set up the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(Objects.requireNonNull(file.getContentType())));
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            log.info(headers.toString());

            // Create the request entity
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // Post the file
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Construct the public URL for the uploaded file
                String fileUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                log.info("File uploaded successfully: {}", fileUrl);
                return fileUrl;
            } else {
                log.error("Failed to upload image to Supabase. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to upload image to Supabase");
            }
        } catch (IOException e) {
            log.error("Error uploading file to Supabase: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) return "";
        return filename.substring(lastDotIndex);
    }
}