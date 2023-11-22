package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ArtistService {
    void deleteArtist(Long id);
    byte[] getArtistProfilePicture(Long id) throws IOException;

    ArtistDtoResponse updateArtist(Long id, ArtistDto artistDto, MultipartFile profilePicture);

    ArtistDtoResponse addArtist(ArtistDto artistDto, MultipartFile profilePicture);

    List<ArtistDtoResponse> getAllArtists();
}
