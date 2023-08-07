package com.example.eventinformationsystembackend.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {
    public void savePictureToFileSystem(MultipartFile picture, String path) {
        try {
            picture.transferTo(new File(path));
        } catch (IOException e) {

        }
    }

    public void createFolder(String path) {
        new File(path).mkdirs();
    }

    public void deleteFile(String path) {
        File fileToDelete = new File(path);
        fileToDelete.delete();
    }

    public void renameFolder(String currentFolderName, String newFolderName) {
        Path currentFolder =
                Paths.get(currentFolderName);
        Path renamedFolder =
                Paths.get(newFolderName);

        try {
            Files.move(currentFolder, renamedFolder);
        } catch (FileAlreadyExistsException e) {
            System.out.println("FILE");
        } catch (IOException e) {
            System.out.println("IO");
        }
    }

    public void deleteFolder(String path) {
        try {
            FileUtils.forceDelete(new File(path));
        } catch (IOException e) {

        }
    }
}
