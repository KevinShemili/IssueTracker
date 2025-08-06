package com.shemilikevin.app.tracker.controller;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.repository.mongo.IssueMongoRepository;
import com.shemilikevin.app.tracker.repository.mongo.ProjectMongoRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueControllerIT {

	private static final String DATABASE_NAME = "db";
	private static final String PROJECT_COLLECTION = "projects";
	private static final String ISSUE_COLLECTION = "issues";
	private static final String ISSUE_NAME = "Broken Button";
	private static final String ISSUE_DESCRIPTION = "Button is not clickable when...";
	private static final String ISSUE_PRIORITY = "Low";
	private static final String PROJECT_ID = "2";
	private static final String ISSUE_ID = "1";

	@Mock
	private IssueTrackerView issueTrackerView;

	private AutoCloseable autoCloseable;
	private ProjectRepository projectRepository;
	private IssueRepository issueRepository;
	private IssueController issueController;

	@ClassRule
	public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:5");
	private MongoClient mongoClient;

	@Before
	public void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		mongoClient = new MongoClient(new ServerAddress(mongoContainer.getHost(), mongoContainer.getFirstMappedPort()));
		projectRepository = new ProjectMongoRepository(mongoClient, DATABASE_NAME, PROJECT_COLLECTION);
		issueRepository = new IssueMongoRepository(mongoClient, DATABASE_NAME, ISSUE_COLLECTION);
		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		database.drop();

		issueController = new IssueController(projectRepository, issueRepository, issueTrackerView);
	}

	@After
	public void tearDown() throws Exception {
		autoCloseable.close();
		mongoClient.close();
	}

	@Test
	public void testListIssues_WhenProjectHasIssues_ShowsAllIssues() {
		// Arrange
		Project project = new Project(PROJECT_ID, "Project Name", "Project Description");
		projectRepository.save(project);

		Issue issue = new Issue("1", "Issue Name", "Issue Description", "Low", PROJECT_ID);
		Issue issue2 = new Issue("2", "Issue Name", "Issue Description", "Low", PROJECT_ID);
		issueRepository.save(issue);
		issueRepository.save(issue2);

		// Act
		issueController.listIssues(PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssues(Arrays.asList(issue, issue2));
	}

	@Test
	public void testAddIssue_WhenProvidedFieldsAreValid_CreatesNewIssue() {
		// Arrange
		String newId = "2";
		String newName = "Name";
		String newDescription = "Description";
		String newPriority = "Low";

		Project project = new Project(PROJECT_ID, "Project Name", "Project Description");
		projectRepository.save(project);

		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		issueRepository.save(issue);

		// Act
		issueController.addIssue(newId, newName, newDescription, newPriority, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssues(
				Arrays.asList(new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID),
						new Issue(newId, newName, newDescription, newPriority, PROJECT_ID)));
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdAlreadyExistInDatabase_ShowsDuplicationError() {
		// Arrange
		Project project = new Project(PROJECT_ID, "Project Name", "Project Description");
		projectRepository.save(project);

		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		issueRepository.save(issue);

		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, ISSUE_ID));
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsValid_DeletesIssue() {
		// Arrange
		Project project = new Project(PROJECT_ID, "Project Name", "Project Description");
		projectRepository.save(project);

		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		issueRepository.save(issue);

		// Act
		issueController.deleteIssue(ISSUE_ID);

		// Assert
		verify(issueTrackerView).showIssues(Collections.emptyList());
	}
}
