package com.example.issuetracker.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.dto.request.ProjectMemberAddRequest;
import com.example.issuetracker.dto.response.ProjectMemberResponse;
import com.example.issuetracker.entity.Project;
import com.example.issuetracker.entity.ProjectMember;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.ProjectMemberRepository;
import com.example.issuetracker.repository.ProjectRepository;
import com.example.issuetracker.repository.UserRepository;
import com.example.issuetracker.service.ProjectMemberService;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectAuthorizationService projectAuthorizationService;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        Project project = findProject(projectId);
        projectAuthorizationService.requireProjectMember(project.getId());

        return projectMemberRepository.findAllByProject_Id(project.getId())
                .stream()
                .map(ProjectMemberResponse::new)
                .toList();
    }

    @Override
    @Transactional
    public ProjectMemberResponse addProjectMember(Long projectId, ProjectMemberAddRequest request) {
        Project project = findProject(projectId);
        projectAuthorizationService.requireProjectOwner(project.getId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found. id=" + request.getUserId()));

        if (projectMemberRepository.existsByProject_IdAndUser_Id(project.getId(), user.getId())) {
            throw new IllegalArgumentException("User is already a project member.");
        }

        ProjectMember projectMember = ProjectMember.member(project, user);
        ProjectMember savedProjectMember = projectMemberRepository.save(projectMember);

        return new ProjectMemberResponse(savedProjectMember);
    }

    @Override
    @Transactional
    public void removeProjectMember(Long projectId, Long userId) {
        Project project = findProject(projectId);
        projectAuthorizationService.requireProjectOwner(project.getId());

        ProjectMember projectMember = projectMemberRepository.findByProject_IdAndUser_Id(project.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found."));

        if (projectMember.isOwner()) {
            throw new IllegalArgumentException("Project owner cannot be removed.");
        }

        projectMemberRepository.delete(projectMember);
    }

    private Project findProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found. id=" + projectId));
    }
}
