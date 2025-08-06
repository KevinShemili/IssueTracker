package com.shemilikevin.app.tracker.repository;

import java.util.List;

import com.shemilikevin.app.tracker.model.Project;

public interface ProjectRepository {

	boolean exists(String id);

	List<Project> findAll();

	Project findById(String id);

	void save(Project project);

	void delete(String id);
}
