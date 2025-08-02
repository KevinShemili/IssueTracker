package com.shemilikevin.app.tracker.view;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;

public interface IssueTrackerView {

	void showIssues(List<Issue> issueList);

	void showError(String errorMessage);
}
