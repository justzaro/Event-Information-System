package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.repository.ArtistRepository;
import com.example.eventinformationsystembackend.service.ArtistService;
import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final StorageService storageService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<ArtistDtoResponse> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();

        return artists
               .stream()
               .map(artist -> modelMapper.map(artist, ArtistDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
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

    @Override
    public ArtistDtoResponse updateArtist(Long id, ArtistDto artistDto, MultipartFile profilePicture) {
        Artist artist = getArtistOrThrowException(id);

        if (profilePicture != null) {
            if (!profilePicture.isEmpty()) {
                storageService.deleteFile(artist.getProfilePicturePath());

                String updatedProfilePicturePath = ARTISTS_FOLDER_PATH +
                        artist.getFirstName() + " " + artist.getLastName() + "\\" +
                        profilePicture.getOriginalFilename();

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

    @Override
    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = getArtistOrThrowException(id);

        for (Event event : artist.getEvents()) {
            event.getArtists().remove(artist);
        }

        artistRepository.delete(artist);
    }

    @Override
    public byte[] getArtistProfilePicture(Long id) throws IOException {
        Artist artist = getArtistOrThrowException(id);
        String profilePicturePath = artist.getProfilePicturePath();
        return Files.readAllBytes(new File(profilePicturePath).toPath());
    }

    private Artist getArtistOrThrowException(Long id) {
        return dataValidationService.getResourceByIdOrThrowException(id, Artist.class, ARTIST_DOES_NOT_EXIST);
    }
}
