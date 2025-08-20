package com.shemilikevin.app.tracker.controller;

import java.util.List;

import com.shemilikevin.app.tracker.helpers.ErrorMessages;
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class ProjectController extends BaseController {

	public ProjectController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		super(projectRepository, issueRepository, issueTrackerView);
	}

	public void listProjects() {
		List<Project> projectList = projectRepository.findAll();

		issueTrackerView.showProjects(projectList);
		issueTrackerView.clearProjectFields();
		issueTrackerView.clearProjectSelection();
	}

	public void addProject(String id, String name, String description) {

		if (!validateFields(id, name, description)) {
			return;
		}

		if (isProjectStoredInDatabase(id)) {
			issueTrackerView.showProjectError(String.format(ErrorMessages.DUPLICATE_PROJECT, id));
			return;
		}

		Project project = new Project(id, name, description);
		projectRepository.save(project);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
		issueTrackerView.clearProjectFields();
	}

	public void deleteProject(String id) {

		if (!validateFields(id)) {
			return;
		}

		if (!isProjectStoredInDatabase(id)) {
			issueTrackerView.showProjectError(ErrorMessages.PROJECT_DOESNT_EXIST);
			issueTrackerView.showProjects(projectRepository.findAll()); // refreshes view to remove any stale data
			return;
		}

		if (issueRepository.hasAssociatedIssues(id)) {
			issueTrackerView.showProjectError(ErrorMessages.PROJECT_HAS_ISSUES);
			return;
		}

		projectRepository.delete(id);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
		issueTrackerView.clearProjectSelection();
	}

	private boolean validateFields(String id, String name, String description) {

		if (!validateId(id)) {
			return false;
		}

		if (!validateIsNotNullOrEmpty(name)) {
			issueTrackerView.showProjectError(ErrorMessages.NULL_EMPTY_NAME);
			return false;
		}

		if (!validateIsNotNullOrEmpty(description)) {
			issueTrackerView.showProjectError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
			return false;
		}

		return true;
	}

	private boolean validateFields(String id) {
		return validateId(id);
	}

	private boolean validateId(String id) {

		if (!validateIsNotNullOrEmpty(id)) {
			issueTrackerView.showProjectError(ErrorMessages.NULL_EMPTY_ID);
			return false;
		}

		if (!validateIsNumeric(id)) {
			issueTrackerView.showProjectError(ErrorMessages.NON_NUMERICAL_ID);
			return false;
		}

		return true;
	}
}
