package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.service.StorageService;
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
public class StorageServiceImpl implements StorageService {

    @Override
    public void savePictureToFileSystem(MultipartFile picture, String path) {
        try {
            picture.transferTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createFolder(String path) {
        new File(path).mkdirs();
    }

    public void deleteFile(String path) {
        File fileToDelete = new File(path);
        fileToDelete.delete();
    }

    @Override
    public void renameFolder(String currentFolderName, String newFolderName) {
        Path currentFolder =
                Paths.get(currentFolderName);
        Path renamedFolder =
                Paths.get(newFolderName);

        try {
            Files.move(currentFolder, renamedFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFolder(String path) {
        try {
            FileUtils.forceDelete(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
