package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{username}")
    public List<CommentDtoResponse> getAllCommentsByUser(
            @PathVariable String username) {
        return commentService.getAllCommentsByUser(username);
    }

    @PatchMapping("/{id}/is-read")
    public ResponseEntity<Void> markCommentAsRead(@PathVariable Long id) {
        commentService.markCommentAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/is-removed")
    public ResponseEntity<Void> markCommentAsRemoved(@PathVariable Long id) {
        commentService.markCommentAsRemoved(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/{postId}")
    public CommentDtoResponse addComment(@PathVariable String username,
                                         @PathVariable Long postId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return commentService.addComment(commentDto, postId, username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
