package com.shemilikevin.app.tracker.helpers;

public final class ErrorMessages {

	private ErrorMessages() {
	}

	public static final String NULL_EMPTY_ID = "ID must not be null or empty.";
	public static final String NULL_EMPTY_NAME = "Name must not be null or empty.";
	public static final String NULL_EMPTY_DESCRIPTION = "Description must not be null or empty.";
	public static final String NULL_EMPTY_PRIORITY = "Priority must not be null or empty.";
	public static final String NOT_ALLOWED_PRIORITY = "Issue priority must be either Low, Medium or High.";
	public static final String NON_NUMERICAL_ID = "ID must be numerical.";
	public static final String PROJECT_HAS_ISSUES = "Selected project has associated issues.";
	public static final String DUPLICATE_PROJECT = "Project with ID: %s, already exists.";
	public static final String DUPLICATE_ISSUE = "Issue with ID: %s, already exists.";
	public static final String PROJECT_DOESNT_EXIST = "Project ID does not exist in the database.";
	public static final String ISSUE_DOESNT_EXIST = "Issue ID does not exist in the database.";
}
