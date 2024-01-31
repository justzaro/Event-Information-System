package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.service.CommentService;
import com.example.eventinformationsystembackend.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public List<PostDtoResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{username}/comments")
    public List<CommentDtoResponse> getAllCommentsUnderUsersPosts(@PathVariable String username) {
        return commentService.getAllCommentsUnderUsersPosts(username);
    }

    @GetMapping("/{id}/picture")
    public ResponseEntity<?> getPostPicture(@PathVariable Long id)
            throws IOException {
        byte[] postPicture = postService.getPostPicture(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(postPicture);
    }

    @PostMapping(path = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostDtoResponse addPost(@PathVariable String username,
                                   @RequestPart("postDto") @Valid PostDto postDto,
                                   @RequestPart("postPicture") MultipartFile postPicture) {
        return postService.addPost(postDto, postPicture, username);
    }

    @DeleteMapping("/{id}/{username}")
    public ResponseEntity<Void> deleteOwnedPost(@PathVariable Long id,
                                                @PathVariable String username) {
        postService.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }
}
