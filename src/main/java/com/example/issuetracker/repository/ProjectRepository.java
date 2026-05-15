package com.example.issuetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.issuetracker.entity.Project;
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	
}
