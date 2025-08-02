package com.shemilikevin.app.tracker.view;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;

public interface IssueTrackerView {

	void showIssues(List<Issue> issueList);

	void showError(String errorMessage);

	void showProjects(List<Project> projectList);
}
