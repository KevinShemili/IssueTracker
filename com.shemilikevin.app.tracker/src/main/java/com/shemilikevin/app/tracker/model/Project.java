package com.shemilikevin.app.tracker.model;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Project {

	// Fields
	@BsonProperty("id")
	private String id;
	private String name;
	private String description;

	public Project(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	// Needed for automatic MongoDB deserialization
	public Project() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, name);
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
		Project other = (Project) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
