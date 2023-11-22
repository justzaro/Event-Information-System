package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.PostDto;
import com.example.eventinformationsystembackend.dto.PostDtoResponse;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    void deletePost(Long postId, String username);

    byte[] getPostPicture(Long postId) throws IOException;

    void replaceOldUsernameWithNewOneInPicturePathForAllUserPosts(String oldUsername,
                                                                  String newUsername);

    PostDtoResponse addPost(PostDto postDto, MultipartFile postPicture,
                            String username);

    List<PostDtoResponse> getAllPosts();

    List<PostDtoResponse> getAllPostForUser(User user);
}
