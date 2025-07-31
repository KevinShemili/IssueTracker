package com.shemilikevin.app.tracker.model;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Issue {

	// Fields
	@BsonProperty("id")
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

	// Needed for automatic MongoDB deserialization
	public Issue() {
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

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
