package com.example.issuetracker.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.request.CommentCreateRequest;
import com.example.issuetracker.dto.response.CommentResponse;
import com.example.issuetracker.entity.Comment;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.User;
import com.example.issuetracker.exception.ForbiddenException;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.security.CurrentUserProvider;
import com.example.issuetracker.service.CommentService;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	@Autowired
	private IssueRepository issueRepo;
	@Autowired
	private CommentRepository commentRepo;
	@Autowired
	private CurrentUserProvider currentUserProvider;
	@Autowired
	private ProjectAuthorizationService projectAuthorizationService;

	@Override
	@Transactional
	public CommentResponse createComment(Long issueId, CommentCreateRequest request) {
		Issue currentIssue = findIssue(issueId);
		projectAuthorizationService.requireProjectMember(currentIssue.getProject().getId());
		User currentUser = currentUserProvider.getCurrentUser();

		Comment comment = new Comment();
		comment.setIssue(currentIssue);
		comment.setContent(request.getContent());
		comment.setAuthor(currentUser);
		Comment savedComment = commentRepo.save(comment);

		return CommentResponse.responseDto(savedComment);
	}

	@Override
	public List<CommentResponse> getCommentsByIssue(Long issueId) {
		Issue issue = findIssue(issueId);
		projectAuthorizationService.requireProjectMember(issue.getProject().getId());
		List<Comment> comments = commentRepo.findByIssue_Id(issueId);
		return comments.stream().map(CommentResponse::responseDto).toList();
	}

	@Override
	public CommentResponse getComment(Long commentId) {
		Comment currentComment = findComment(commentId);
		requireCommentReadable(currentComment);
		return CommentResponse.responseDto(currentComment);
	}

	@Override
	@Transactional
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {
		Comment currentComment = findComment(commentId);
		requireCommentManager(currentComment);
		currentComment.setContent(request.getContent());
		Comment savedComment = commentRepo.save(currentComment);
		return CommentResponse.responseDto(savedComment);
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {
		Comment currentComment = findComment(commentId);
		requireCommentManager(currentComment);
		commentRepo.delete(currentComment);

	}

	private Issue findIssue(Long issueId) {
		return issueRepo.findById(issueId)
				.orElseThrow(() -> new ResourceNotFoundException("Issue Id not found " + issueId));
	}

	private Comment findComment(Long commentId) {
		return commentRepo.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment Id not found " + commentId));
	}

	private void requireCommentReadable(Comment comment) {
		projectAuthorizationService.requireProjectMember(comment.getIssue().getProject().getId());
	}

	private void requireCommentManager(Comment comment) {
		Long projectId = comment.getIssue().getProject().getId();
		projectAuthorizationService.requireProjectMember(projectId);

		if (projectAuthorizationService.isProjectOwner(projectId) || isCurrentUser(comment.getAuthor())) {
			return;
		}

		throw new ForbiddenException("Comment author or project owner only.");
	}

	private boolean isCurrentUser(User user) {
		if (user == null) {
			return false;
		}

		return user.getId().equals(currentUserProvider.getCurrentUserId());
	}

}
