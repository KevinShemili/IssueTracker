package com.shemilikevin.app.tracker.repository;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;

public interface IssueRepository {

	List<Issue> findByProjectId(String projectId);

}
