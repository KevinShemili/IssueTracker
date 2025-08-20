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
import com.shemilikevin.app.tracker.helpers.ErrorMessages;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.repository.mongo.IssueMongoRepository;
import com.shemilikevin.app.tracker.repository.mongo.ProjectMongoRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueControllerIT {

	@ClassRule
	public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:5");

	private static final String DATABASE_NAME = "db";
	private static final String PROJECT_COLLECTION = "projects";
	private static final String ISSUE_COLLECTION = "issues";

	@Mock
	private IssueTrackerView issueTrackerView;

	private AutoCloseable autoCloseable;
	private ProjectRepository projectRepository;
	private IssueRepository issueRepository;
	private IssueController issueController;
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
		String projectId = "10";
		Project project = new Project(projectId, "Name", "Description");
		projectRepository.save(project);

		Issue issue = new Issue("1", "Name 1", "Description 1", "Low", projectId);
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Low", projectId);
		issueRepository.save(issue);
		issueRepository.save(issue2);

		// Act
		issueController.listIssues(projectId);

		// Assert
		verify(issueTrackerView).showIssues(Arrays.asList(issue, issue2));
	}

	@Test
	public void testAddIssue_WhenProvidedFieldsAreValid_CreatesNewIssue() {
		// Arrange
		String id = "2";
		String name = "Name";
		String description = "Description";
		String priority = "Low";
		String projectId = "10";

		Project project = new Project(projectId, "Name", "Description");
		projectRepository.save(project);

		// Act
		issueController.addIssue(id, name, description, priority, projectId);

		// Assert
		verify(issueTrackerView).showIssues(
				Arrays.asList(new Issue(id, name, description, priority, projectId)));
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdAlreadyExistInDatabase_ShowErrorMessage() {
		// Arrange
		String id = "2";
		String projectId = "10";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		issueRepository.save(new Issue(id, "Name", "Description", "Priority", projectId));

		// Act
		issueController.addIssue(id, "Name", "Description", "Low", projectId); // duplicate id

		// Assert
		verify(issueTrackerView).showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, id));
	}

	@Test
	public void testAddIssue_WhenProvidedNonNumericId_ShowErrorMessage() {
		// Arrange
		String projectId = "10";
		projectRepository.save(new Project(projectId, "Name", "Description"));

		// Act
		issueController.addIssue("XYZ", "Name", "Description", "Low", projectId);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsValid_DeletesIssue() {
		// Arrange
		String id = "1";
		String projectId = "10";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		issueRepository.save(new Issue(id, "Name", "Description", "Priority", projectId));

		// Act
		issueController.deleteIssue(id, projectId);

		// Assert
		verify(issueTrackerView).showIssues(Collections.emptyList());
	}
}
