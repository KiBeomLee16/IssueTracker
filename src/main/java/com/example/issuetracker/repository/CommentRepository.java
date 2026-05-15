package com.example.issuetracker.repository;

import com.example.issuetracker.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssue_Id(Long issueId);
    
    long countByIssue_Project_Id(Long projectId);
}