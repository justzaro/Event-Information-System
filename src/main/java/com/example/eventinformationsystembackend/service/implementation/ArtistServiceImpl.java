package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.repository.ArtistRepository;
import com.example.eventinformationsystembackend.service.ArtistService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final StorageServiceImpl storageServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository,
                             StorageServiceImpl storageServiceImpl) {
        this.artistRepository = artistRepository;
        this.storageServiceImpl = storageServiceImpl;
        this.modelMapper = new ModelMapper();
    }

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

        storageServiceImpl.createFolder(artistFolderPath);
        storageServiceImpl.savePictureToFileSystem(profilePicture, profilePicturePath);

        return modelMapper.map(artistRepository.save(artistToAdd), ArtistDtoResponse.class);
    }

    @Override
    public ArtistDtoResponse updateArtist(Long id, ArtistDto artistDto, MultipartFile profilePicture) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTIST_DOES_NOT_EXIST));

        if (profilePicture != null) {
            if (!profilePicture.isEmpty()) {
                storageServiceImpl.deleteFile(artist.getProfilePicturePath());

                String updatedProfilePicturePath = ARTISTS_FOLDER_PATH +
                        artist.getFirstName() + " " + artist.getLastName() + "\\" +
                        profilePicture.getOriginalFilename();

                storageServiceImpl.savePictureToFileSystem(profilePicture, updatedProfilePicturePath);
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
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTIST_DOES_NOT_EXIST));

        for (Event event : artist.getEvents()) {
            event.getArtists().remove(artist);
        }

        artistRepository.delete(artist);
    }

    @Override
    public byte[] getArtistProfilePicture(Long id) throws IOException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTIST_DOES_NOT_EXIST));
        String profilePicturePath = artist.getProfilePicturePath();
        return Files.readAllBytes(new File(profilePicturePath).toPath());
    }
}
