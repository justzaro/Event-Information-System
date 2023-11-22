package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.CommentServiceImpl;
import com.example.eventinformationsystembackend.service.implementation.PostServiceImpl;
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
@RequestMapping("/posts")
public class PostController {
    private final PostServiceImpl postServiceImpl;
    private final CommentServiceImpl commentServiceImpl;

    @Autowired
    public PostController(PostServiceImpl postServiceImpl,
                          CommentServiceImpl commentServiceImpl) {
        this.postServiceImpl = postServiceImpl;
        this.commentServiceImpl = commentServiceImpl;
    }

    @GetMapping
    public List<PostDtoResponse> getAllPosts() {
        return postServiceImpl.getAllPosts();
    }

    @GetMapping("/{username}/comments")
    public List<CommentDtoResponse> getAllCommentsUnderUsersPosts(@PathVariable String username) {
        return commentServiceImpl.getAllCommentsUnderUsersPosts(username);
    }

    @GetMapping("/{id}/picture")
    public ResponseEntity<?> getPostPicture(@PathVariable Long id)
            throws IOException {
        byte[] postPicture = postServiceImpl.getPostPicture(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(postPicture);
    }

    @PostMapping(path = "/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostDtoResponse addPost(@PathVariable String username,
                                   @RequestPart("postDto") @Valid PostDto postDto,
                                   @RequestPart("postPicture") MultipartFile postPicture) {
        return postServiceImpl.addPost(postDto, postPicture, username);
    }

    @DeleteMapping("/{id}/{username}")
    public ResponseEntity<Void> deleteOwnedPost(@PathVariable Long id,
                                                @PathVariable String username) {
        postServiceImpl.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }
}
