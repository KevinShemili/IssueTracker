package com.shemilikevin.app.tracker.repository;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;

public interface IssueRepository {

	List<Issue> findByProjectId(String projectId);

	boolean exists(String issueId);

	void save(Issue issue);

	Issue findById(String issueId);

	void delete(String issueId);

	boolean hasAssociatedIssues(String projectId);
}
