package com.example.issuetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.request.CommentCreateRequest;
import com.example.issuetracker.dto.response.CommentResponse;
import com.example.issuetracker.entity.Comment;
import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.exception.ResourceNotFoundException;
import com.example.issuetracker.repository.CommentRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.serviceImpl.CommentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private IssueRepository issueRepo;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Issue issue;
    private Comment comment;

    @BeforeEach
    void setUp() {
        issue = createIssueEntity();
        comment = createCommentEntity();

        ReflectionTestUtils.setField(issue, "id", 1L);

        ReflectionTestUtils.setField(comment, "id", 1L);
        ReflectionTestUtils.setField(comment, "content", "Test comment");
        ReflectionTestUtils.setField(comment, "issue", issue);
    }

    @Test
    void createComment_success() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "Test comment");

        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));

        when(commentRepo.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);

            ReflectionTestUtils.setField(savedComment, "id", 1L);

            return savedComment;
        });

        // when
        CommentResponse response = commentService.createComment(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Test comment", response.getContent());

        verify(issueRepo).findById(1L);
        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void createComment_issueNotFound() {
        // given
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request, "content", "Test comment");

        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(999L, request);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void getComments_success() {
        // given
        when(issueRepo.findById(1L)).thenReturn(Optional.of(issue));
        when(commentRepo.findByIssue_Id(1L)).thenReturn(List.of(comment));

        // when
        List<CommentResponse> response = commentService.getCommentsByIssue(1L);

        // then
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("Test comment", response.get(0).getContent());

        verify(issueRepo).findById(1L);
        verify(commentRepo).findByIssue_Id(1L);
    }

    @Test
    void getComments_issueNotFound() {
        // given
        when(issueRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentsByIssue(999L);
        });

        verify(issueRepo).findById(999L);
    }

    @Test
    void getComment_success() {
        // given
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        // when
        CommentResponse response = commentService.getComment(1L);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Test comment", response.getContent());

        verify(commentRepo).findById(1L);
    }

    @Test
    void getComment_notFound() {
        // given
        when(commentRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getComment(999L);
        });

        verify(commentRepo).findById(999L);
    }

    @Test
    void updateComment_success() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest();
        ReflectionTestUtils.setField(request, "content", "Updated comment");

        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        when(commentRepo.save(any(Comment.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // when
        CommentResponse response = commentService.updateComment(1L, request);

        // then
        assertEquals(1L, response.getId());
        assertEquals("Updated comment", response.getContent());

        verify(commentRepo).findById(1L);
        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void updateComment_notFound() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest();
        ReflectionTestUtils.setField(request, "content", "Updated comment");

        when(commentRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(999L, request);
        });

        verify(commentRepo).findById(999L);
    }

    @Test
    void deleteComment_success() {
        // given
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepo).delete(comment);

        // when
        commentService.deleteComment(1L);

        // then
        verify(commentRepo).findById(1L);
        verify(commentRepo).delete(comment);
    }

    @Test
    void deleteComment_notFound() {
        // given
        when(commentRepo.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(999L);
        });

        verify(commentRepo).findById(999L);
    }

    private Issue createIssueEntity() {
        try {
            Constructor<Issue> constructor = Issue.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Comment createCommentEntity() {
        try {
            Constructor<Comment> constructor = Comment.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}