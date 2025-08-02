package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController {

	private List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");

	private ProjectRepository projectRepository;
	private IssueRepository issueRepository;
	private IssueTrackerView issueTrackerView;

	public IssueController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		this.issueRepository = issueRepository;
		this.projectRepository = projectRepository;
		this.issueTrackerView = issueTrackerView;
	}

	public void listIssues(String projectId) {

		validateId(projectId, "Project ID");
		validateProjectExists(projectId);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		validateId(issueId, "Issue ID");
		validateId(projectId, "Project ID");
		validateIsNullOrEmpty(issueName, "Issue name");
		validateIsNullOrEmpty(issueDescription, "Issue description");
		validatePriorityIsAllowed(issuePriority, "Issue priority");
		validateProjectExists(projectId);

		if (issueRepository.exists(issueId) == true) {
			issueTrackerView.showError("Issue with ID: " + issueId + ", already exists.");
			return;
		}

		Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
		issueRepository.save(issue);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void deleteIssue(String issueId) {

		validateId(issueId, "Issue ID");
		validateIssueExists(issueId);

		Issue toBeDeleted = issueRepository.findById(issueId);
		issueRepository.delete(issueId);

		List<Issue> issueList = issueRepository.findByProjectId(toBeDeleted.getProjectId());
		issueTrackerView.showIssues(issueList);
	}

	private void validateIsNullOrEmpty(String string, String fieldName) {
		if ((string == null) || (string.trim().isEmpty() == true)) {
			throw new IllegalArgumentException(fieldName + " must not be null or empty.");
		}
	}

	private void validateIsNumeric(String string, String fieldName) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(fieldName + " must be numerical.");
		}
	}

	private void validatePriorityIsAllowed(String priority, String fieldName) {
		validateIsNullOrEmpty(priority, fieldName);

		if (ALLOWED_PRIORITIES.contains(priority) == false) {
			throw new IllegalArgumentException(fieldName + " must be either Low, Medium or High.");
		}
	}

	private void validateId(String projectId, String fieldName) {
		validateIsNullOrEmpty(projectId, fieldName);
		validateIsNumeric(projectId, fieldName);
	}

	private void validateProjectExists(String projectId) {
		if (projectRepository.exists(projectId) == false) {
			throw new IllegalArgumentException("Project ID does not exist in the database.");
		}
	}

	private void validateIssueExists(String issueId) {
		if (issueRepository.exists(issueId) == false) {
			throw new IllegalArgumentException("Issue ID does not exist in the database.");
		}
	}
}
