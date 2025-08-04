package com.shemilikevin.app.tracker.controller;

import java.util.List;

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
	}

	public void addProject(String id, String name, String description) {

		Validators.validateProjectFields(id, name, description);

		if (isNumeric(id) == false) {
			issueTrackerView.showProjectError(Validators.NON_NUMERICAL_ID);
			return;
		}

		if (isProjectStoredInDatabase(id) == true) {
			issueTrackerView.showProjectError(String.format(Validators.DUPLICATE_PROJECT, id));
			return;
		}

		Project project = new Project(id, name, description);
		projectRepository.save(project);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}

	public void deleteProject(String id) {

		Validators.validateId(id);
		Validators.validateIsNumeric(id);

		if (isProjectStoredInDatabase(id) == false) {
			throw new IllegalArgumentException(Validators.PROJECT_DOESNT_EXIST);
		}

		if (issueRepository.hasAssociatedIssues(id) == true) {
			issueTrackerView.showProjectError(Validators.PROJECT_HAS_ISSUES);
			return;
		}

		projectRepository.delete(id);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}
}
