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
}
