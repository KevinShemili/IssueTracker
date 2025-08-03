package com.shemilikevin.app.tracker.controller;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController {

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

		Validators.validateProjectId(projectId);
		validateProjectExists(projectId);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		Validators.validateNullOrEmptyIssueId(issueId);
		Validators.validateIssueName(issueName);
		Validators.validateIssueDescription(issueDescription);
		Validators.validatePriority(issuePriority);
		Validators.validateNullOrEmptyProjectId(projectId);

		try {
			Integer.parseInt(issueId);
		} catch (NumberFormatException e) {
			issueTrackerView.showIssueError("Issue ID must be numerical.");
			return;
		}

		try {
			Integer.parseInt(projectId);
		} catch (NumberFormatException e) {
			issueTrackerView.showIssueError("Project ID must be numerical.");
			return;
		}

		validateProjectExists(projectId);

		if (issueRepository.exists(issueId) == true) {
			issueTrackerView.showIssueError("Issue with ID: " + issueId + ", already exists.");
			return;
		}

		Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
		issueRepository.save(issue);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void deleteIssue(String issueId) {

		Validators.validateIssueId(issueId);
		validateIssueExists(issueId);

		Issue toBeDeleted = issueRepository.findById(issueId);
		issueRepository.delete(issueId);

		List<Issue> issueList = issueRepository.findByProjectId(toBeDeleted.getProjectId());
		issueTrackerView.showIssues(issueList);
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
