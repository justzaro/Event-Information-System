package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findArtistByFirstNameAndLastName(String firstName,
                                                      String lastName);
}
