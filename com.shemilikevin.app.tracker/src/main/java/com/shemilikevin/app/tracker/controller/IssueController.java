package com.shemilikevin.app.tracker.controller;

import java.util.List;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueController extends BaseController {

	public IssueController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		super(projectRepository, issueRepository, issueTrackerView);
	}

	public void listIssues(String projectId) {

		Validators.validateId(projectId);
		Validators.validateIsNumeric(projectId);

		if (isProjectStoredInDatabase(projectId) == false) {
			throw new IllegalArgumentException(Validators.PROJECT_DOESNT_EXIST);
		}

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void addIssue(String issueId, String issueName, String issueDescription, String issuePriority,
			String projectId) {

		Validators.validateIssueFields(issueId, issueName, issueDescription, issuePriority, projectId);

		if (isNumeric(issueId) == false) {
			issueTrackerView.showIssueError(Validators.NON_NUMERICAL_ID);
			return;
		}

		if (isNumeric(projectId) == false) {
			throw new IllegalArgumentException(Validators.NON_NUMERICAL_ID);
		}

		if (isProjectStoredInDatabase(projectId) == false) {
			throw new IllegalArgumentException(Validators.PROJECT_DOESNT_EXIST);
		}

		if (isIssueStoredInDatabase(issueId) == true) {
			issueTrackerView.showIssueError(String.format(Validators.DUPLICATE_ISSUE, issueId));
			return;
		}

		Issue issue = new Issue(issueId, issueName, issueDescription, issuePriority, projectId);
		issueRepository.save(issue);

		List<Issue> issueList = issueRepository.findByProjectId(projectId);
		issueTrackerView.showIssues(issueList);
	}

	public void deleteIssue(String issueId) {

		Validators.validateId(issueId);
		Validators.validateIsNumeric(issueId);

		if (isIssueStoredInDatabase(issueId) == false) {
			throw new IllegalArgumentException(Validators.ISSUE_DOESNT_EXIST);
		}

		Issue toBeDeleted = issueRepository.findById(issueId);
		issueRepository.delete(issueId);

		List<Issue> issueList = issueRepository.findByProjectId(toBeDeleted.getProjectId());
		issueTrackerView.showIssues(issueList);
	}

	private boolean isIssueStoredInDatabase(String id) {
		return issueRepository.exists(id) == true ? true : false;
	}
}
