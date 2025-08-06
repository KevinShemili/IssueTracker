package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController extends BaseController {

	public static final List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");

	public IssueController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		super(projectRepository, issueRepository, issueTrackerView);
	}

	public void listIssues(String projectId) {

		if (!validateFields(projectId)) {
			return;
		}

		if (isProjectStoredInDatabase(projectId) == false) {
			issueTrackerView.showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
			return;
		}

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		if (!validateFields(issueId, issueName, issueDescription, issuePriority, projectId)) {
			return;
		}

		if (isProjectStoredInDatabase(projectId) == false) {
			issueTrackerView.showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
			return;
		}

		if (isIssueStoredInDatabase(issueId) == true) {
			issueTrackerView.showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, issueId));
			return;
		}

		Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
		issueRepository.save(issue);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void deleteIssue(String issueId) {

		if (!validateFields(issueId)) {
			return;
		}

		if (isIssueStoredInDatabase(issueId) == false) {
			issueTrackerView.showIssueError(ErrorMessages.ISSUE_DOESNT_EXIST);
			return;
		}

		Issue toBeDeleted = issueRepository.findById(issueId);
		issueRepository.delete(issueId);

		List<Issue> issueList = issueRepository.findByProjectId(toBeDeleted.getProjectId());
		issueTrackerView.showIssues(issueList);
	}

	private boolean isIssueStoredInDatabase(String id) {
		return issueRepository.exists(id) == true ? true : false;
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

		if (ALLOWED_PRIORITIES.contains(priority) == false) {
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
