package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.ArtistDto;
import com.example.eventinformationsystembackend.dto.ArtistDtoResponse;
import com.example.eventinformationsystembackend.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(path = "/add")
    public ArtistDtoResponse addArtist(@RequestPart @Valid ArtistDto artistDto,
                                       @RequestPart MultipartFile profilePicture) {
        return artistService.addArtist(artistDto, profilePicture);
    }
}
