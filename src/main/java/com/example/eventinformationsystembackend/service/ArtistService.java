package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;
import static com.example.eventinformationsystembackend.common.UserInformation.DEFAULT_USER_PROFILE_PICTURE_NAME;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    @Autowired
    public ArtistService(ArtistRepository artistRepository,
                         StorageService storageService) {
        this.artistRepository = artistRepository;
        this.storageService = storageService;
        this.modelMapper = new ModelMapper();
    }

    public List<ArtistDtoResponse> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();

        return artists
               .stream()
               .map(artist -> modelMapper.map(artist, ArtistDtoResponse.class))
               .collect(Collectors.toList());
    }

    public ArtistDtoResponse addArtist(ArtistDto artistDto,
                                       MultipartFile profilePicture) {
        if (artistRepository.findArtistByFirstNameAndLastName(artistDto.getFirstName(),
                                                              artistDto.getLastName()).isPresent()) {
            throw new DuplicateUniqueFieldException(ARTIST_ALREADY_EXISTS);
        }

        Artist artistToAdd = modelMapper.map(artistDto, Artist.class);

        String artistFolderPath =
                ARTISTS_FOLDER_PATH + artistDto.getFirstName() + " " + artistDto.getLastName();
        String profilePicturePath = artistFolderPath + "\\" + profilePicture.getOriginalFilename();

        artistToAdd.setProfilePicturePath(profilePicturePath);

        storageService.createFolder(artistFolderPath);
        storageService.savePictureToFileSystem(profilePicture, profilePicturePath);

        return modelMapper.map(artistRepository.save(artistToAdd), ArtistDtoResponse.class);
    }

    public ArtistDtoResponse updateArtist(Long id, ArtistDto artistDto, MultipartFile profilePicture) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTIST_DOES_NOT_EXIST));

        System.out.println(profilePicture == null);

        if (profilePicture != null) {
            if (!profilePicture.isEmpty()) {
                storageService.deleteFile(artist.getProfilePicturePath());

                String updatedProfilePicturePath = ARTISTS_FOLDER_PATH +
                        artist.getFirstName() + " " + artist.getLastName() + "\\" +
                        profilePicture.getOriginalFilename();

                System.out.println(updatedProfilePicturePath);
                System.out.println(profilePicture.getName());
                System.out.println(profilePicture.getOriginalFilename());

                storageService.savePictureToFileSystem(profilePicture, updatedProfilePicturePath);
                artist.setProfilePicturePath(updatedProfilePicturePath);
            }
        }

        artist.setFirstName(artistDto.getFirstName());
        artist.setLastName(artistDto.getLastName());
        artist.setDescription(artistDto.getDescription());

        artist = artistRepository.save(artist);
        return modelMapper.map(artist, ArtistDtoResponse.class);
    }

    public byte[] getArtistProfilePicture(Long id) throws IOException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTIST_DOES_NOT_EXIST));
        String profilePicturePath = artist.getProfilePicturePath();
        return Files.readAllBytes(new File(profilePicturePath).toPath());
    }
}
