package com.shemilikevin.app.tracker.model;

import java.util.Objects;

public class Issue {

	// Fields
	private String id;
	private String title;
	private String description;
	private String priority;

	// Reference
	private String projectId;

	public Issue(String id, String title, String description, String priority, String projectId) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.projectId = projectId;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPriority() {
		return priority;
	}

	public String getProjectId() {
		return projectId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, priority, projectId, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Issue other = (Issue) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(priority, other.priority) && Objects.equals(projectId, other.projectId)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Issue [id=" + id + ", title=" + title + ", description=" + description + ", priority=" + priority
				+ ", projectId=" + projectId + "]";
	}
}
