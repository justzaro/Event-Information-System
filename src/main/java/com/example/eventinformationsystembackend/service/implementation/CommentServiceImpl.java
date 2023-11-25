package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Comment;
import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CommentRepository;
import com.example.eventinformationsystembackend.service.CommentService;
import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public CommentDtoResponse addComment(CommentDto commentDto,
                                         Long id,
                                         String username) {
        Post post = dataValidationService.getResourceByIdOrThrowException(id, Post.class, POST_DOES_NOT_EXIST);
        User user = getUserOrThrowException(username);

        Comment commentToAdd = modelMapper.map(commentDto, Comment.class);

        commentToAdd.setUser(user);
        commentToAdd.setPost(post);
        commentToAdd.setPostedAt(LocalDateTime.now());
        commentToAdd.setIsRead(false);
        commentToAdd.setIsRemoved(false);

        Comment newComment = commentRepository.save(commentToAdd);
        return modelMapper.map(newComment, CommentDtoResponse.class);
    }

    @Override
    public List<CommentDtoResponse> getAllCommentsByUser(String username) {
        User user = getUserOrThrowException(username);

        List<Comment> comments = commentRepository.findAllByUser(user);

        return comments
               .stream()
               .map(comment -> modelMapper.map(comment, CommentDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public List<CommentDtoResponse> getAllCommentsUnderUsersPosts(String username) {
        User user = getUserOrThrowException(username);

        List<PostDtoResponse> userPosts = postService.getAllPostForUser(user);
        List<CommentDtoResponse> allCommentsUnderUserPosts = new ArrayList<>();

        for (PostDtoResponse post : userPosts) {
            allCommentsUnderUserPosts.addAll(post.getComments());
        }

        allCommentsUnderUserPosts.sort(Comparator.comparing(CommentDtoResponse::getPostedAt).reversed());

        return allCommentsUnderUserPosts;
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_DOES_NOT_EXIST));

        commentRepository.delete(comment);
    }

    @Override
    public void markCommentAsRead(Long id) {
        Comment comment = getCommentOrThrowException(id);
        comment.setIsRead(true);
        commentRepository.save(comment);
    }

    @Override
    public void markCommentAsRemoved(Long id) {
        Comment comment = getCommentOrThrowException(id);
        comment.setIsRemoved(true);
        commentRepository.save(comment);
    }

    private Comment getCommentOrThrowException(Long id) {
        return dataValidationService.
                getResourceByIdOrThrowException(id, Comment.class, COMMENT_DOES_NOT_EXIST);
    }

    private User getUserOrThrowException(String username) {
        return dataValidationService.getUserByUsername(username);
    }
}
