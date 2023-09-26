package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Post;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserOrderByPostedAtAsc(User user);
}
