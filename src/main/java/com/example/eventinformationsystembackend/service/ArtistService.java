package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.enums.Currency;
import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.exception.DuplicateUniqueFieldException;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
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

        artistToAdd.setProfilePicturePath(artistFolderPath + "\\" + profilePicture.getOriginalFilename());

        new File(artistFolderPath).mkdirs();

        try {
            uploadEventPictureToFileSystem(profilePicture,
                    artistFolderPath + "\\" + profilePicture.getOriginalFilename());
        } catch (IOException e) {

        }

        return modelMapper.map(artistRepository.save(artistToAdd), ArtistDtoResponse.class);

    }

    private void uploadEventPictureToFileSystem(MultipartFile profilePicture,
                                                String path) throws IOException {
        profilePicture.transferTo(new File(path));
    }
}
