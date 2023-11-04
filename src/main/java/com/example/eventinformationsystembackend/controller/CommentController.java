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

    @PatchMapping(path = "/{commentId}/is-read")
    public ResponseEntity<Void> markCommentAsRead(@PathVariable("commentId") Long commentId) {
        commentService.markCommentAsRead(commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/{commentId}/is-removed")
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
