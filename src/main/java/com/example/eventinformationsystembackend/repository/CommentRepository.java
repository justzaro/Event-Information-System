package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.model.Comment;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUser(User user);
}
