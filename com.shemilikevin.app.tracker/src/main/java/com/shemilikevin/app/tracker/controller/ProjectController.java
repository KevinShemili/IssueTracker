package com.shemilikevin.app.tracker.controller;

import java.util.List;

import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class ProjectController {

	private ProjectRepository projectRepository;
	private IssueRepository issueRepository;
	private IssueTrackerView issueTrackerView;

	public ProjectController(ProjectRepository projectRepository, IssueRepository issueRepository,
			IssueTrackerView issueTrackerView) {

		this.projectRepository = projectRepository;
		this.issueRepository = issueRepository;
		this.issueTrackerView = issueTrackerView;
	}

	public void listProjects() {
		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}

	public void addProject(String id, String name, String description) {

		Validators.validateProjectId(id);
		Validators.validateProjectName(name);
		Validators.validateProjectDescription(description);

		if (projectRepository.exists(id) == true) {
			issueTrackerView.showError("Project with ID: " + id + ", already exists.");
			return;
		}

		Project project = new Project(id, name, description);
		projectRepository.save(project);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}

	public void deleteProject(String id) {

		Validators.validateProjectId(id);
		validateProjectExists(id);

		if (issueRepository.hasAssociatedIssues(id) == true) {
			issueTrackerView.showError("Selected project has associated issues.");
			return;
		}

		projectRepository.delete(id);

		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}

	private void validateProjectExists(String projectId) {
		if (projectRepository.exists(projectId) == false) {
			throw new IllegalArgumentException("Project ID does not exist in the database.");
		}
	}
}
