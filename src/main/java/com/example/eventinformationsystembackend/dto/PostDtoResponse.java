package com.example.eventinformationsystembackend.dto;

import com.example.eventinformationsystembackend.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoResponse {
    private Long postId;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime postedAt;

    @NotBlank
    private String postPicturePath;

    private UserDtoResponse user;

    private List<CommentDtoResponse> comments;
}
