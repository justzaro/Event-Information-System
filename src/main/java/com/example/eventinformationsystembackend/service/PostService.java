package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.exception.PostDoesNotContainImageException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Comment;
import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.PostRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       StorageService storageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        modelMapper = new ModelMapper();
    }

    public List<PostDtoResponse> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();

        return allPosts
               .stream()
               .map(post -> modelMapper.map(post, PostDtoResponse.class))
               .collect(Collectors.toList());
    }

    public List<PostDtoResponse> getAllPostForUser(User user) {
        List<Post> allPosts = postRepository.findAllByUserOrderByPostedAtAsc(user);

        return allPosts
                .stream()
                .map(post -> modelMapper.map(post, PostDtoResponse.class))
                .collect(Collectors.toList());
    }

    public PostDtoResponse addPost(PostDto postDto, MultipartFile postPicture,
                        String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        //Maybe remove the exception if posts can be text-only
        if (postPicture.isEmpty()) {
            throw new PostDoesNotContainImageException(POST_DOES_NOT_CONTAIN_IMAGE);
        }

        Post postToAdd = modelMapper.map(postDto, Post.class);

        String postPicturePath = USERS_FOLDER_PATH + user.getUsername() + "\\Posts\\"
                + postPicture.getOriginalFilename();

        postToAdd.setPostedAt(LocalDateTime.now());
        postToAdd.setUser(user);
        postToAdd.setPostPicturePath(postPicturePath);

        storageService.savePictureToFileSystem(postPicture, postPicturePath);

        return modelMapper.map(postRepository.save(postToAdd), PostDtoResponse.class);
    }

    public void deleteOwnedPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        //add equals method for User object
        if (!post.getUser().getUsername().equals(user.getUsername())) {
            throw new  IllegalStateException("post now owned by user");
        }

        postRepository.delete(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));

        postRepository.delete(post);
    }

    public byte[] getPostPicture(Long postId) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));
        String postPicturePath = post.getPostPicturePath();
        return Files.readAllBytes(new File(postPicturePath).toPath());
    }

    public void replaceOldUsernameWithNewOneInPicturePathForAllUserPosts(String oldUsername,
                                                                         String newUsername) {
        User user = userRepository.findUserByUsername(oldUsername)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<PostDtoResponse> allUserPosts = getAllPostForUser(user);

        for (PostDtoResponse post : allUserPosts) {
            String updatedPath = post.getPostPicturePath().replace(oldUsername, newUsername);
            post.setPostPicturePath(updatedPath);
            postRepository.save(modelMapper.map(post, Post.class));
        }
    }
}
