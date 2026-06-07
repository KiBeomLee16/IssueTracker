package com.example.issuetracker.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.issuetracker.entity.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {

	List<Label> findAllByProject_IdOrderByNameAsc(Long projectId);

	List<Label> findAllByIdInAndProject_Id(Collection<Long> ids, Long projectId);

	boolean existsByProject_IdAndName(Long projectId, String name);
}
