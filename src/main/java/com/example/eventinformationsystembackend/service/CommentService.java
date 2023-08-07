package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CommentDto;
import com.example.eventinformationsystembackend.dto.CommentDtoResponse;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
}
