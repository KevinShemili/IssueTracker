package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController {

	private List<String> PRIORITIES = Arrays.asList("Low", "Medium", "High");

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

		validateProjectId(projectId);
		checkProjectExists(projectId);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	private void validateProjectId(String projectId) {
		if ((projectId == null) || (projectId.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Project ID must not be null or empty.");
		}

		try {
			Integer.parseInt(projectId);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Project ID must be numerical.");
		}
	}

	private void checkProjectExists(String projectId) {
		if (projectRepository.exists(projectId) == false) {
			throw new IllegalArgumentException("Project ID does not exist in the database.");
		}
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		if ((issueId == null) || (issueId.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Issue ID must not be null or empty.");
		}

		if ((issueName == null) || (issueName.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Issue name must not be null or empty.");
		}

		if ((issueDescription == null) || (issueDescription.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Issue description must not be null or empty.");
		}

		if ((issuePriority == null) || (issuePriority.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Issue priority must not be null or empty.");
		}

		if (PRIORITIES.contains(issuePriority) == false) {
			throw new IllegalArgumentException("Issue priority must be either Low, Medium or High.");
		}

		if ((projectRepository.exists(projectId) == true && (issueRepository.exists(issueId)) == false)) {
			Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
			issueRepository.save(issue);
			List<Issue> issueList = issueRepository.findByProjectId(projectId);
			issueTrackerView.showIssues(issueList);
		}
	}

}
