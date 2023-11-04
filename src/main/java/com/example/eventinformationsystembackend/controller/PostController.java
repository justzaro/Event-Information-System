package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.service.CommentService;
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
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService,
                          CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public List<PostDtoResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping(path = "/{username}/comments")
    public List<CommentDtoResponse> getAllCommentsUnderUsersPosts(@PathVariable("username") String username) {
        return commentService.getAllCommentsUnderUsersPosts(username);
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

    @PostMapping(path = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostDtoResponse addPost(@PathVariable("username") String username,
                                   @RequestPart("postDto") @Valid PostDto postDto,
                                   @RequestPart("postPicture") MultipartFile postPicture) {
        return postService.addPost(postDto, postPicture, username);
    }

    @DeleteMapping(path = "/{postId}/{username}")
    public ResponseEntity<Void> deleteOwnedPost(@PathVariable("postId") Long postId,
                                                @PathVariable("username") String username) {
        postService.deletePost(postId, username);
        return ResponseEntity.noContent().build();
    }
}
