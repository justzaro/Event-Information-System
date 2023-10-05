package com.example.eventinformationsystembackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoResponse {

    @NotBlank
    private Long id;

    @NotBlank
    private Long postId;

    @NotBlank
    private String commentBody;

    @NotBlank
    private Boolean isRead;

    @NotBlank
    private Boolean isRemoved;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private LocalDateTime postedAt;

    private UserDtoResponse user;
}
