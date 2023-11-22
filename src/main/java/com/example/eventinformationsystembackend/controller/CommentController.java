package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentServiceImpl commentServiceImpl;

    @Autowired
    public CommentController(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

    @GetMapping("/{username}")
    public List<CommentDtoResponse> getAllCommentsByUser(
            @PathVariable String username) {
        return commentServiceImpl.getAllCommentsByUser(username);
    }

    @PatchMapping("/{id}/is-read")
    public ResponseEntity<Void> markCommentAsRead(@PathVariable Long id) {
        commentServiceImpl.markCommentAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/is-removed")
    public ResponseEntity<Void> markCommentAsRemoved(@PathVariable Long id) {
        commentServiceImpl.markCommentAsRemoved(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/{postId}")
    public CommentDtoResponse addComment(@PathVariable String username,
                                         @PathVariable Long postId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return commentServiceImpl.addComment(commentDto, postId, username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentServiceImpl.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
