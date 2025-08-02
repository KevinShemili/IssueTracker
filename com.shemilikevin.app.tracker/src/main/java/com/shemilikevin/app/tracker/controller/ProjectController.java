package com.shemilikevin.app.tracker.controller;

import java.util.List;

import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class ProjectController {

	private ProjectRepository projectRepository;
	private IssueTrackerView issueTrackerView;

	public ProjectController(ProjectRepository projectRepository, IssueTrackerView issueTrackerView) {
		this.projectRepository = projectRepository;
		this.issueTrackerView = issueTrackerView;
	}

	public void listProjects() {
		List<Project> projectList = projectRepository.findAll();
		issueTrackerView.showProjects(projectList);
	}

	public void addProject(String id, String name, String description) {

		if ((id == null) || (id.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Project ID must not be null or empty.");
		}

		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Project ID must be numerical.");
		}

		if ((name == null) || (name.trim().isEmpty() == true)) {
			throw new IllegalArgumentException("Project name must not be null or empty.");
		}

		if (projectRepository.exists(id) == false) {
			Project project = new Project(id, name, description);
			projectRepository.save(project);

			List<Project> projectList = projectRepository.findAll();
			issueTrackerView.showProjects(projectList);
		}
	}
}
