package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.dto.UserDtoResponse;
import com.example.eventinformationsystembackend.dto.UserUpdateDto;
import com.example.eventinformationsystembackend.model.Artist;
import com.example.eventinformationsystembackend.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/artists")
public class ArtistController {
    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping()
    public List<ArtistDtoResponse> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping(path = "/profile-picture/{id}")
    public ResponseEntity<?> getArtistProfilePicture(
            @PathVariable("id") Long id) throws IOException {
        byte[] profilePicture = artistService.getArtistProfilePicture(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(profilePicture);
    }

    @PostMapping
    public ArtistDtoResponse addArtist(@RequestPart @Valid ArtistDto artistDto,
                                       @RequestPart(value = "profilePicture") MultipartFile profilePicture) {
        return artistService.addArtist(artistDto, profilePicture);
    }

    @PutMapping(path = "/{id}")
    public ArtistDtoResponse updateArtist(@PathVariable("id") Long id,
                                          @RequestPart @Valid ArtistDto artistDto,
                                          @RequestPart(required = false) MultipartFile profilePicture) {
        return artistService.updateArtist(id, artistDto, profilePicture);
    }
}
