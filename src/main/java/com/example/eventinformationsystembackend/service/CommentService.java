package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;

import java.util.List;

public interface CommentService {
    void deleteComment(Long commentId);

    void markCommentAsRead(Long commentId);

    void markCommentAsRemoved(Long commentId);

    CommentDtoResponse addComment(CommentDto commentDto, Long postId, String username);

    List<CommentDtoResponse> getAllCommentsByUser(String username);

    List<CommentDtoResponse> getAllCommentsUnderUsersPosts(String username);
}
