package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Comment;
import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CommentRepository;
import com.example.eventinformationsystembackend.repository.PostRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          PostService postService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
        modelMapper = new ModelMapper();
    }

    public CommentDtoResponse addComment(CommentDto commentDto,
                                         Long postId,
                                         String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Comment commentToAdd = modelMapper.map(commentDto, Comment.class);

        commentToAdd.setUser(user);
        commentToAdd.setPost(post);
        commentToAdd.setPostedAt(LocalDateTime.now());
        commentToAdd.setIsRead(false);
        commentToAdd.setIsRemoved(false);

        Comment newComment = commentRepository.save(commentToAdd);
        return modelMapper.map(newComment, CommentDtoResponse.class);
    }

    public List<CommentDtoResponse> getAllCommentsByUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<Comment> comments = commentRepository.findAllByUser(user);

        return comments
               .stream()
               .map(comment -> modelMapper.map(comment, CommentDtoResponse.class))
               .collect(Collectors.toList());
    }

    public List<CommentDtoResponse> getAllCommentsUnderUsersPosts(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<PostDtoResponse> userPosts = postService.getAllPostForUser(user);
        List<CommentDtoResponse> allCommentsUnderUserPosts = new ArrayList<>();

        for (PostDtoResponse post : userPosts) {
            allCommentsUnderUserPosts.addAll(post.getComments());
        }

        allCommentsUnderUserPosts.sort(Comparator.comparing(CommentDtoResponse::getPostedAt).reversed());

        //todo 1. refine all mappings 2. add a function which gets all comments, sorted, instead of the sorting above

        return allCommentsUnderUserPosts;
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_DOES_NOT_EXIST));

        commentRepository.delete(comment);
    }

    public void markCommentAsRead(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_DOES_NOT_EXIST));

        comment.setIsRead(true);
        commentRepository.save(comment);
    }

    public void markCommentAsRemoved(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_DOES_NOT_EXIST));

        comment.setIsRemoved(true);
        commentRepository.save(comment);
    }
}
