package com.example.eventinformationsystembackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {
    void savePictureToFileSystem(MultipartFile picture, String path);

    void createFolder(String path);

    void deleteFile(String path);

    void renameFolder(String currentFolderName, String newFolderName);

    void deleteFolder(String path);
}
