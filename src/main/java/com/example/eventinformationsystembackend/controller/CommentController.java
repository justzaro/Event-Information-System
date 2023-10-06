package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(path = "/{username}")
    public List<CommentDtoResponse> getAllCommentsByUser(
            @PathVariable("username") String username) {
        return commentService.getAllCommentsByUser(username);
    }

    //if it is done using a single DTO to which holds all changes, won't this
    //create unnecessary memory usage since every time I have to send all the fields
    //I want/might change - comment body, is-read, is-removed fields and send
    //their original values if I don't want to change them?
    //Also, I won't know which value is their original value, so I have to somehow
    //check everytime for their original value
    @PutMapping(path = "/{commentId}/is-read")
    public ResponseEntity<Void> markCommentAsRead(@PathVariable("commentId") Long commentId) {
        commentService.markCommentAsRead(commentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{commentId}/is-removed")
    public ResponseEntity<Void> markCommentAsRemoved(@PathVariable("commentId") Long commentId) {
        commentService.markCommentAsRemoved(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{username}/{postId}")
    public CommentDtoResponse addComment(@PathVariable("username") String username,
                                         @PathVariable("postId") Long postId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return commentService.addComment(commentDto, postId, username);
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
