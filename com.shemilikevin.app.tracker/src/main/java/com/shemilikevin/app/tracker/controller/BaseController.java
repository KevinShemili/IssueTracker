package com.shemilikevin.app.tracker.controller;

import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

abstract class BaseController {

	protected final ProjectRepository projectRepository;
	protected final IssueRepository issueRepository;
	protected final IssueTrackerView issueTrackerView;

	protected BaseController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		this.projectRepository = projectRepository;
		this.issueRepository = issueRepository;
		this.issueTrackerView = issueTrackerView;
	}

	protected boolean validateIsNumeric(String id) {
		try {
			Integer.parseInt(id);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected boolean validateIsNotNullOrEmpty(String id) {
		if ((id == null) || (id.trim().isEmpty() == true)) {
			return false;
		}

		return true;
	}

	protected boolean isProjectStoredInDatabase(String id) {
		return projectRepository.exists(id) == true ? true : false;
	}
}
