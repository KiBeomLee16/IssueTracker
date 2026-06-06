package com.example.issuetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.issuetracker.entity.IssueHistory;

public interface IssueHistoryRepository extends JpaRepository<IssueHistory, Long> {

	List<IssueHistory> findByIssue_IdOrderByCreatedAtDescIdDesc(Long issueId);
}
