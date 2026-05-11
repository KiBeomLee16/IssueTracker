package com.example.issuetracker.repository;

import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssuePriority;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
	public List<Issue> findByProject_Id(Long projectId);

	public List<Issue> findByProject(Project project);

	@Query("""
			SELECT i FROM Issue i WHERE 
			  i.project.id = :projectId
			  AND (:status IS NULL OR i.status = :status)
			  AND (:priority IS NULL OR i.priority = :priority)
			  AND (:keyword IS NULL
			        OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			      )
			""")
	public Page<Issue> searchIssuesByProject(@Param("projectId") Long projectId, @Param("status") IssueStatus status,
			@Param("priority") IssuePriority priority, @Param("keyword") String keyword, Pageable pageable);

}
