package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.List;

class Validators {

	private Validators() {
	}

	static final List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");
	static final String NULL_EMPTY_ID = "ID must not be null or empty.";
	static final String NULL_EMPTY_NAME = "Name must not be null or empty.";
	static final String NULL_EMPTY_DESCRIPTION = "Description must not be null or empty.";
	static final String NULL_EMPTY_PRIORITY = "Priority must not be null or empty.";
	static final String NOT_ALLOWED_PRIORITY = "Issue priority must be either Low, Medium or High.";
	static final String NON_NUMERICAL_ID = "ID must be numerical.";
	static final String PROJECT_HAS_ISSUES = "Selected project has associated issues.";
	static final String DUPLICATE_PROJECT = "Project with ID: %s, already exists.";
	static final String DUPLICATE_ISSUE = "Issue with ID: %s, already exists.";
	static final String PROJECT_DOESNT_EXIST = "Project ID does not exist in the database.";
	static final String ISSUE_DOESNT_EXIST = "Issue ID does not exist in the database.";

	static void validateProjectFields(String id, String name, String description) {
		validateIsNotNullOrEmpty(id, NULL_EMPTY_ID);
		validateIsNotNullOrEmpty(name, NULL_EMPTY_NAME);
		validateIsNotNullOrEmpty(description, NULL_EMPTY_DESCRIPTION);
	}

	static void validateId(String id) {
		validateIsNotNullOrEmpty(id, NULL_EMPTY_ID);
	}

	static void validateIssueFields(String issueId, String name, String description, String priority,
			String projectId) {
		validateId(issueId);
		validateIsNotNullOrEmpty(name, NULL_EMPTY_NAME);
		validateIsNotNullOrEmpty(description, NULL_EMPTY_DESCRIPTION);
		validateIsNotNullOrEmpty(priority, NULL_EMPTY_PRIORITY);

		if (ALLOWED_PRIORITIES.contains(priority) == false) {
			throw new IllegalArgumentException(NOT_ALLOWED_PRIORITY);
		}

		validateId(projectId);
	}

	static void validateIsNumeric(String id) {
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(NON_NUMERICAL_ID);
		}
	}

	private static void validateIsNotNullOrEmpty(String id, String errorMessage) {
		if ((id == null) || (id.trim().isEmpty() == true)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

}
