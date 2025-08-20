package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.shemilikevin.app.tracker.helpers.ErrorMessages;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController extends BaseController {

	private static final List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");

	public IssueController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		super(projectRepository, issueRepository, issueTrackerView);
	}

	public void listIssues(String projectId) {

		if (!validateFields(projectId)) {
			return;
		}

		if (!isProjectStoredInDatabase(projectId)) {
			issueTrackerView.showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
			issueTrackerView.showIssues(Collections.emptyList());
			return;
		}

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
		issueTrackerView.clearIssueFields();
		issueTrackerView.clearIssueSelection();
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		if (!validateFields(issueId, issueName, issueDescription, issuePriority, projectId)) {
			return;
		}

		if (!isProjectStoredInDatabase(projectId)) {
			issueTrackerView.showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
			return;
		}

		if (isIssueStoredInDatabase(issueId)) {
			issueTrackerView.showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, issueId));
			return;
		}

		Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
		issueRepository.save(issue);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
		issueTrackerView.clearIssueFields();
	}

	public void deleteIssue(String issueId, String projectId) {

		if (!validateFields(issueId)) {
			return;
		}

		if (!isIssueStoredInDatabase(issueId)) {
			issueTrackerView.showIssueError(ErrorMessages.ISSUE_DOESNT_EXIST);
			issueTrackerView.showIssues(issueRepository.findByProjectId(projectId));
			return;
		}

		Issue toBeDeleted = issueRepository.findById(issueId);
		issueRepository.delete(issueId);

		List<Issue> issueList = issueRepository.findByProjectId(toBeDeleted.getProjectId());
		issueTrackerView.showIssues(issueList);
		issueTrackerView.clearIssueSelection();
	}

	private boolean isIssueStoredInDatabase(String id) {
		return issueRepository.exists(id);
	}

	private boolean validateFields(String id, String name, String description, String priority, String projectId) {

		if (!validateId(id) || !validateId(projectId)) {
			return false;
		}

		if (!validateIsNotNullOrEmpty(name)) {
			issueTrackerView.showIssueError(ErrorMessages.NULL_EMPTY_NAME);
			return false;
		}

		if (!validateIsNotNullOrEmpty(description)) {
			issueTrackerView.showIssueError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
			return false;
		}

		if (!validateIsNotNullOrEmpty(priority)) {
			issueTrackerView.showIssueError(ErrorMessages.NULL_EMPTY_PRIORITY);
			return false;
		}

		if (!ALLOWED_PRIORITIES.contains(priority)) {
			issueTrackerView.showIssueError(ErrorMessages.NOT_ALLOWED_PRIORITY);
			return false;
		}

		return true;
	}

	private boolean validateFields(String id) {
		return validateId(id);
	}

	private boolean validateId(String id) {
		if (!validateIsNotNullOrEmpty(id)) {
			issueTrackerView.showIssueError(ErrorMessages.NULL_EMPTY_ID);
			return false;
		}

		if (!validateIsNumeric(id)) {
			issueTrackerView.showIssueError(ErrorMessages.NON_NUMERICAL_ID);
			return false;
		}

		return true;
	}
}
