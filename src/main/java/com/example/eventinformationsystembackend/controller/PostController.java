package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.service.PostService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDtoResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping(path = "/picture/{postId}")
    public ResponseEntity<?> getPostPicture(@PathVariable("postId") Long postId)
            throws IOException {
        byte[] postPicture = postService.getPostPicture(postId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(postPicture);
    }

    @PostMapping(path = "/add/{username}")
    public PostDtoResponse addPost(@PathVariable("username") String username,
                                   @RequestPart @Valid PostDto postDto,
                                   @RequestPart MultipartFile postPicture) {
        return postService.addPost(postDto, postPicture, username);
    }

    @DeleteMapping(path = "/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
