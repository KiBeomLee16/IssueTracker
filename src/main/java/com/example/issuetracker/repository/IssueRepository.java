package com.example.issuetracker.repository;


import com.example.issuetracker.entity.Issue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
	public  List<Issue>  findByProject_Id (Long projectId);
}
