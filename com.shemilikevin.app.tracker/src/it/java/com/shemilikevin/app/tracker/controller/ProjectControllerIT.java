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

public class ProjectControllerIT {

	private static final String DATABASE_NAME = "db";
	private static final String PROJECT_COLLECTION = "projects";
	private static final String ISSUE_COLLECTION = "issues";

	@Mock
	private IssueTrackerView issueTrackerView;

	private AutoCloseable autoCloseable;
	private ProjectRepository projectRepository;
	private IssueRepository issueRepository;
	private ProjectController projectController;

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

		projectController = new ProjectController(projectRepository, issueRepository, issueTrackerView);
	}

	@After
	public void tearDown() throws Exception {
		autoCloseable.close();
		mongoClient.close();
	}

	@Test
	public void testListProjects_WhenThereAreProjectsInTheDatabase_ShowsAllProjects() {
		// Arrange
		Project project = new Project("1", "Name", "Description");
		projectRepository.save(project);

		// Act
		projectController.listProjects();

		// Assert
		verify(issueTrackerView).showProjects(Arrays.asList(project));
	}

	@Test
	public void testAddProject_WhenProvidedFieldsAreValid_CreatesNewProject() {
		// Arrange
		String id = "1";
		String name = "Name";
		String description = "Description";

		// Act
		projectController.addProject(id, name, description);

		// Assert
		verify(issueTrackerView).showProjects(
				Arrays.asList(new Project(id, name, description)));
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdAlreadyExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String id = "1";
		projectRepository.save(new Project(id, "Name", "Description"));

		// Act
		projectController.addProject(id, "Name", "Description"); // duplicate id

		// Assert
		verify(issueTrackerView).showProjectError(String.format(ErrorMessages.DUPLICATE_PROJECT, id));
	}

	@Test
	public void testAddProject_WhenProvidedNonNumericId_ShowsErrorMessage() {
		// Act
		projectController.addProject("XYZ", "Name", "Description");

		// Assert
		verify(issueTrackerView).showProjectError(ErrorMessages.NON_NUMERICAL_ID);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasNoAssociatedIssues_DeletesProject() {
		// Arrange
		String id = "1";
		projectRepository.save(new Project(id, "Name", "Description"));

		// Act
		projectController.deleteProject(id);

		// Assert
		verify(issueTrackerView).showProjects(Collections.emptyList());
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasAssociatedIssues_ShowsErrorMessage() {
		// Arrange
		String projectId = "10";
		String issueId = "1";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		issueRepository.save(new Issue(issueId, "Name", "Description", "Priority", projectId));

		// Act
		projectController.deleteProject(projectId);

		// Assert
		verify(issueTrackerView).showProjectError(ErrorMessages.PROJECT_HAS_ISSUES);
	}
}
