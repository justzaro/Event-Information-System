package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "/add/{username}/{postId}")
    public CommentDtoResponse addComment(@PathVariable("username") String username,
                                         @PathVariable("postId") Long postId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return commentService.addComment(commentDto, postId, username);
    }
}
