package com.example.issuetracker.repository;

import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

	boolean existsByProject_IdAndUser_Id(Long projectId, Long userId);

	boolean existsByProject_IdAndUser_IdAndRole(Long projectId, Long userId, ProjectMemberRole role);

	Optional<ProjectMember> findByProject_IdAndUser_Id(Long projectId, Long userId);

	List<ProjectMember> findAllByProject_Id(Long projectId);

	List<ProjectMember> findAllByUser_Id(Long userId);

	void deleteByProject_IdAndUser_Id(Long projectId, Long userId);
}