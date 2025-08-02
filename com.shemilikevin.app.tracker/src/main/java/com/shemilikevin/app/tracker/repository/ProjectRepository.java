package com.shemilikevin.app.tracker.repository;

import java.util.List;

import com.shemilikevin.app.tracker.model.Project;

public interface ProjectRepository {

	boolean exists(String projectId);

	List<Project> findAll();

}
