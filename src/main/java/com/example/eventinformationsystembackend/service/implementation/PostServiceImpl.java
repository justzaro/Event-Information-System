package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.exception.ForbiddenException;
import com.example.eventinformationsystembackend.exception.PostDoesNotContainImageException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.PostRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.PostService;
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
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StorageServiceImpl storageServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           StorageServiceImpl storageServiceImpl) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.storageServiceImpl = storageServiceImpl;
        modelMapper = new ModelMapper();
    }

    @Override
    public List<PostDtoResponse> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();

        return allPosts
               .stream()
               .map(post -> modelMapper.map(post, PostDtoResponse.class))
               .collect(Collectors.toList());
    }

    @Override
    public List<PostDtoResponse> getAllPostForUser(User user) {
        List<Post> allPosts = postRepository.findAllByUserOrderByPostedAtAsc(user);

        return allPosts
                .stream()
                .map(post -> modelMapper.map(post, PostDtoResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDtoResponse addPost(PostDto postDto, MultipartFile postPicture,
                        String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (postPicture.isEmpty()) {
            throw new PostDoesNotContainImageException(POST_DOES_NOT_CONTAIN_IMAGE);
        }

        Post postToAdd = modelMapper.map(postDto, Post.class);

        String postPicturePath = USERS_FOLDER_PATH + user.getUsername() + "\\Posts\\"
                + postPicture.getOriginalFilename();

        postToAdd.setPostedAt(LocalDateTime.now());
        postToAdd.setUser(user);
        postToAdd.setPostPicturePath(postPicturePath);

        storageServiceImpl.savePictureToFileSystem(postPicture, postPicturePath);

        return modelMapper.map(postRepository.save(postToAdd), PostDtoResponse.class);
    }

    @Override
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        if (!post.getUser().getUsername().equals(username)
                || !user.getUserRole().equals(UserRole.ADMIN)) {
            throw new ForbiddenException(RESOURCE_ACCESS_FORBIDDEN);
        }

        postRepository.delete(post);
    }

    public byte[] getPostPicture(Long postId) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_DOES_NOT_EXIST));
        String postPicturePath = post.getPostPicturePath();
        return Files.readAllBytes(new File(postPicturePath).toPath());
    }

    @Override
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
