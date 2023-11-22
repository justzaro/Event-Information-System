package com.example.eventinformationsystembackend.service;

public interface ImageService {
    void createResizedImage(String originalImagePath, String resizedImagePath, String imageExtension);
}
