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
	private static final String ID = "1";
	private static final String NAME = "Desktop Application";
	private static final String DESCRIPTION = "Desktop Application Description";

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
		Project project = new Project(ID, NAME, DESCRIPTION);
		projectRepository.save(project);

		// Act
		projectController.listProjects();

		// Assert
		verify(issueTrackerView).showProjects(Arrays.asList(project));
	}

	@Test
	public void testAddProject_WhenProvidedFieldsAreValid_CreatesNewProject() {
		// Arrange
		String newId = "2";
		String newName = "Name";
		String newDescription = "Description";

		Project project = new Project(ID, NAME, DESCRIPTION);
		projectRepository.save(project);

		// Act
		projectController.addProject(newId, newName, newDescription);

		// Assert
		verify(issueTrackerView).showProjects(
				Arrays.asList(new Project(ID, NAME, DESCRIPTION), new Project(newId, newName, newDescription)));
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasNoAssociatedIssues_DeletesProject() {
		// Arrange
		Project project = new Project(ID, NAME, DESCRIPTION);
		projectRepository.save(project);

		// Act
		projectController.deleteProject(ID);

		// Assert
		verify(issueTrackerView).showProjects(Collections.emptyList());
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasAssociatedIssues_ShowsHasIssuesError() {
		// Arrange
		Project project = new Project(ID, NAME, DESCRIPTION);
		projectRepository.save(project);

		Issue issue = new Issue("1", "Issue Name", "Issue Description", "Low", ID);
		issueRepository.save(issue);

		// Act
		projectController.deleteProject(ID);

		// Assert
		verify(issueTrackerView).showProjectError(ErrorMessages.PROJECT_HAS_ISSUES);
	}
}
