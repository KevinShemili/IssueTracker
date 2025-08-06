package com.shemilikevin.app.tracker.repository;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;

public interface IssueRepository {

	List<Issue> findAll();

	List<Issue> findByProjectId(String projectId);

	boolean exists(String id);

	void save(Issue issue);

	Issue findById(String id);

	void delete(String id);

	boolean hasAssociatedIssues(String projectId);
}
