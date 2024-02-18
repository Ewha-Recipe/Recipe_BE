package com.example.teamproject.controller;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String filePath = "images/" + fileName;
            String fileUrl = "https://" + bucket + "/" + filePath;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, filePath, file.getInputStream(), metadata);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFileFromUrl(@RequestParam("url") String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String bucket = uri.getHost().substring(0, uri.getHost().indexOf("."));
            String key = uri.getPath().substring(1); // Remove leading '/'

            S3Object s3Object = amazonS3Client.getObject(bucket, key);
            InputStream inputStream = s3Object.getObjectContent();
            ByteArrayResource resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
                    .body(resource);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}