package com.shemilikevin.app.tracker.controller;

import java.util.Arrays;
import java.util.List;

public class Validators {

	private Validators() {
	}

	private static final List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");
	private static final String PROJECT_ID_NULL_ERROR = "Project ID must not be null or empty.";
	private static final String ISSUE_ID_NULL_ERROR = "Issue ID must not be null or empty.";
	private static final String PROJECT_NAME_NULL_ERROR = "Project name must not be null or empty.";
	private static final String ISSUE_NAME_NULL_ERROR = "Issue name must not be null or empty.";
	private static final String PROJECT_DESCRIPTION_NULL_ERROR = "Project description must not be null or empty.";
	private static final String ISSUE_DESCRIPTION_NULL_ERROR = "Issue description must not be null or empty.";
	private static final String ISSUE_PRIORITY_NULL_ERROR = "Issue priority must not be null or empty.";
	private static final String ISSUE_PRIORITY_NOT_ALLOWED_ERROR = "Issue priority must be either Low, Medium or High.";
	private static final String PROJECT_ID_NUMERICAL_ERROR = "Project ID must be numerical.";
	private static final String ISSUE_ID_NUMERICAL_ERROR = "Issue ID must be numerical.";

	static void validateProjectId(String id) {
		validateIsNotNullOrEmpty(id, PROJECT_ID_NULL_ERROR);
		validateIsNumeric(id, PROJECT_ID_NUMERICAL_ERROR);
	}

	static void validateIssueId(String id) {
		validateIsNotNullOrEmpty(id, ISSUE_ID_NULL_ERROR);
		validateIsNumeric(id, ISSUE_ID_NUMERICAL_ERROR);
	}

	static void validateProjectName(String name) {
		validateIsNotNullOrEmpty(name, PROJECT_NAME_NULL_ERROR);
	}

	static void validateIssueName(String name) {
		validateIsNotNullOrEmpty(name, ISSUE_NAME_NULL_ERROR);
	}

	static void validateProjectDescription(String description) {
		validateIsNotNullOrEmpty(description, PROJECT_DESCRIPTION_NULL_ERROR);
	}

	static void validateIssueDescription(String description) {
		validateIsNotNullOrEmpty(description, ISSUE_DESCRIPTION_NULL_ERROR);
	}

	static void validatePriority(String priority) {
		validateIsNotNullOrEmpty(priority, ISSUE_PRIORITY_NULL_ERROR);

		if (ALLOWED_PRIORITIES.contains(priority) == false) {
			throw new IllegalArgumentException(ISSUE_PRIORITY_NOT_ALLOWED_ERROR);
		}
	}

	private static void validateIsNotNullOrEmpty(String id, String errorMessage) {
		if ((id == null) || (id.trim().isEmpty() == true)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	private static void validateIsNumeric(String id, String errorMessage) {
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
