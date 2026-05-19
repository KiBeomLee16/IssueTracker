package com.example.issuetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.issuetracker.dto.CommentCreateRequest;
import com.example.issuetracker.dto.UpdateRequest.CommentUpdateRequest;
import com.example.issuetracker.dto.response.CommentResponse;
import com.example.issuetracker.service.CommentService;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void createComment_success() throws Exception {
        // given
        String requestBody = """
                {
                  "content": "First comment",
                  "authorId": 1
                }
                """;

        CommentResponse response = createCommentResponse(
                1L,
                "First comment",
                1L
        );

        when(commentService.createComment(eq(1L), any(CommentCreateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/issues/{issueId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("First comment"))
                .andExpect(jsonPath("$.data.issueId").value(1));

        verify(commentService).createComment(eq(1L), any(CommentCreateRequest.class));
    }

    @Test
    void getCommentsByIssue_success() throws Exception {
        // given
        CommentResponse comment1 = createCommentResponse(
                1L,
                "First comment",
                1L
        );

        CommentResponse comment2 = createCommentResponse(
                2L,
                "Second comment",
                1L
        );

        when(commentService.getCommentsByIssue(1L))
                .thenReturn(List.of(comment1, comment2));

        // when & then
        mockMvc.perform(get("/api/issues/{issueId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comments retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].content").value("First comment"))
                .andExpect(jsonPath("$.data[0].issueId").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].content").value("Second comment"))
                .andExpect(jsonPath("$.data[1].issueId").value(1));

        verify(commentService).getCommentsByIssue(1L);
    }

    @Test
    void getComment_success() throws Exception {
        // given
        CommentResponse response = createCommentResponse(
                1L,
                "First comment",
                1L
        );

        when(commentService.getComment(1L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/comments/{commentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("First comment"))
                .andExpect(jsonPath("$.data.issueId").value(1));

        verify(commentService).getComment(1L);
    }

    @Test
    void updateComment_success() throws Exception {
        // given
        String requestBody = """
                {
                  "content": "Updated comment"
                }
                """;

        CommentResponse response = createCommentResponse(
                1L,
                "Updated comment",
                1L
        );

        when(commentService.updateComment(eq(1L), any(CommentUpdateRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("Updated comment"))
                .andExpect(jsonPath("$.data.issueId").value(1));

        verify(commentService).updateComment(eq(1L), any(CommentUpdateRequest.class));
    }

    @Test
    void deleteComment_success() throws Exception {
        // given
        doNothing().when(commentService).deleteComment(1L);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));

        verify(commentService).deleteComment(1L);
    }

    private CommentResponse createCommentResponse(
            Long id,
            String content,
            Long issueId
    ) {
        return CommentResponse.builder()
                .id(id)
                .content(content)
                .issueId(issueId)
                .createdAt(LocalDateTime.of(2030, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2030, 1, 1, 10, 0))
                .build();
    }
}