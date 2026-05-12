package com.example.issuetracker.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.issuetracker.dto.CommentCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.response.CommentResponse;
import com.example.issuetracker.entity.Comment;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private IssueRepository issueRepo; 
	@Autowired
	private CommentRepository commentRepo ; 
	@Override
	public CommentResponse createComment(Long issueId, CommentCreateRequest request) {
		Issue currentIssue = issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue Id not found " + issueId ) );
		Comment comment = new Comment();
		comment.setIssue(currentIssue);
		comment.setContent(request.getContent());
		Comment savedComment = commentRepo.save(comment);

	    return CommentResponse.responseDto(savedComment);
	}

	@Override
	public List<CommentResponse> getCommentsByIssue(Long issueId) {
		issueRepo.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue Id not found " + issueId));
		List<Comment> comments = commentRepo.findByIssue_Id(issueId);
		return comments.stream().map(CommentResponse::responseDto).toList();
	}

	@Override
	public CommentResponse getComment(Long commentId) {
		Comment currentComment = commentRepo.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment Id not found " + commentId ));
		return CommentResponse.responseDto(currentComment);
	}

	@Override
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {
		Comment currentComment = commentRepo.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment Id not found " + commentId ));
		currentComment.setContent(request.getContent());
		Comment savedComment = commentRepo.save(currentComment);
	    return CommentResponse.responseDto(savedComment);
	}

	@Override
	public void deleteComment(Long commentId) {
		Comment currentComment = commentRepo.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment Id not found " + commentId ));
		commentRepo.delete(currentComment);
		
	}
	

}