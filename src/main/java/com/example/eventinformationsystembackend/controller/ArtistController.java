package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.exception.RequiredPictureMissingException;
import com.example.eventinformationsystembackend.service.ArtistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.REQUIRED_PICTURE_IS_MISSING;

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;

    @GetMapping()
    public List<ArtistDtoResponse> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/profile-picture/{id}")
    public ResponseEntity<?> getArtistProfilePicture(
            @PathVariable Long id) throws IOException {
        byte[] profilePicture = artistService.getArtistProfilePicture(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(profilePicture);
    }

    @PostMapping
    public ArtistDtoResponse addArtist(@RequestPart @Valid ArtistDto artistDto,
                                       @RequestPart(value = "profilePicture") MultipartFile profilePicture) {
        if (profilePicture == null) {
            throw new RequiredPictureMissingException(REQUIRED_PICTURE_IS_MISSING);
        }

        return artistService.addArtist(artistDto, profilePicture);
    }

    @PutMapping("/{id}")
    public ArtistDtoResponse updateArtist(@PathVariable Long id,
                                          @RequestPart @Valid ArtistDto artistDto,
                                          @RequestPart(required = false) MultipartFile profilePicture) {
        return artistService.updateArtist(id, artistDto, profilePicture);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
