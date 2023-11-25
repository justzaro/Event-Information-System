package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.common.enums.UserRole;

import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;

import com.example.eventinformationsystembackend.exception.ForbiddenException;
import com.example.eventinformationsystembackend.exception.PostDoesNotContainImageException;

import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;

import com.example.eventinformationsystembackend.repository.PostRepository;

import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.PostService;
import com.example.eventinformationsystembackend.service.StorageService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final StorageService storageService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

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
        User user = dataValidationService.getUserByUsername(username);

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

    @Override
    public void deletePost(Long id, String username) {
        Post post = getPost(id);
        User user = dataValidationService.getUserByUsername(username);

        if (!post.getUser().getUsername().equals(username)
                || !user.getUserRole().equals(UserRole.ADMIN)) {
            throw new ForbiddenException(RESOURCE_ACCESS_FORBIDDEN);
        }

        postRepository.delete(post);
    }

    public byte[] getPostPicture(Long id) throws IOException {
        Post post = getPost(id);
        String postPicturePath = post.getPostPicturePath();
        return Files.readAllBytes(new File(postPicturePath).toPath());
    }

    @Override
    public void replaceOldUsernameWithNewOneInPicturePathForAllUserPosts(String oldUsername,
                                                                         String newUsername) {
        User user = dataValidationService.getUserByUsername(oldUsername);

        List<PostDtoResponse> allUserPosts = getAllPostForUser(user);

        for (PostDtoResponse post : allUserPosts) {
            String updatedPath = post.getPostPicturePath().replace(oldUsername, newUsername);
            post.setPostPicturePath(updatedPath);
            postRepository.save(modelMapper.map(post, Post.class));
        }
    }

    private Post getPost(Long id) {
        return dataValidationService.
                getResourceByIdOrThrowException(id, Post.class, POST_DOES_NOT_EXIST);
    }
}
