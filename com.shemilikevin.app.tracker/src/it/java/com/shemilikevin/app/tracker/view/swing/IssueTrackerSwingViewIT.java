package com.shemilikevin.app.tracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.shemilikevin.app.tracker.controller.IssueController;
import com.shemilikevin.app.tracker.controller.ProjectController;
import com.shemilikevin.app.tracker.helpers.ErrorMessages;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.repository.mongo.IssueMongoRepository;
import com.shemilikevin.app.tracker.repository.mongo.ProjectMongoRepository;

@RunWith(GUITestRunner.class)
public class IssueTrackerSwingViewIT extends AssertJSwingJUnitTestCase {

	@ClassRule
	public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:5");

	private static final String DATABASE_NAME = "db";
	private static final String PROJECT_COLLECTION = "projects";
	private static final String ISSUE_COLLECTION = "issues";

	private static final int TAB_ISSUES = 1;
	private static final String TABBED_PANE = "tabbedPane";
	private static final String PROJECT_ID_FIELD = "projectIdField";
	private static final String PROJECT_NAME_FIELD = "projectNameField";
	private static final String PROJECT_DESCRIPTION_FIELD = "projectDescriptionField";
	private static final String PROJECT_ADD_BUTTON = "addProjectButton";
	private static final String PROJECT_DELETE_BUTTON = "deleteProjectButton";
	private static final String PROJECT_LIST = "projectList";
	private static final String PROJECT_ERROR_LABEL = "projectErrorLabel";
	private static final String ISSUE_ID_FIELD = "issueIdField";
	private static final String ISSUE_NAME_FIELD = "issueNameField";
	private static final String ISSUE_DESCRIPTION_FIELD = "issueDescriptionField";
	private static final String ISSUE_PRIORITY_COMBO = "issuePriorityComboBox";
	private static final String ISSUE_ERROR_LABEL = "issueErrorLabel";
	private static final String ISSUE_LIST = "issueList";
	private static final String ISSUE_ADD_BUTTON = "addIssueButton";
	private static final String ISSUE_DELETE_BUTTON = "deleteIssueButton";

	private MongoClient mongoClient;
	private FrameFixture frameFixture;
	private ProjectController projectController;
	private IssueController issueController;
	private IssueRepository issueRepository;
	private ProjectRepository projectRepository;

	@Override
	protected void onSetUp() throws Exception {
		mongoClient = new MongoClient(new ServerAddress(mongoContainer.getHost(), mongoContainer.getFirstMappedPort()));

		projectRepository = new ProjectMongoRepository(mongoClient, DATABASE_NAME, PROJECT_COLLECTION);
		issueRepository = new IssueMongoRepository(mongoClient, DATABASE_NAME, ISSUE_COLLECTION);

		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		database.drop();

		frameFixture = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
			IssueTrackerSwingView issueTrackerView = new IssueTrackerSwingView();

			projectController = new ProjectController(projectRepository, issueRepository, issueTrackerView);
			issueController = new IssueController(projectRepository, issueRepository, issueTrackerView);

			issueTrackerView.setIssueController(issueController);
			issueTrackerView.setProjectController(projectController);

			return issueTrackerView;
		})).show();
	}

	@Override
	protected void onTearDown() throws Exception {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testProjectTab_ShowsAllProjectsToView() {
		// Arrange
		Project project1 = new Project("1", "Name 1", "Description 1");
		Project project2 = new Project("2", "Name 2", "Description 2");
		projectRepository.save(project1);
		projectRepository.save(project2);

		// Act
		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).containsExactly(project1.toString(), project2.toString());
	}

	@Test
	@GUITest
	public void testAddProjectButton_HappyPath_CreatesProject() {
		// Arrange
		String id = "1";
		String name = "Project";
		String description = "Description";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(name);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(description);

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).containsExactly(new Project(id, name, description).toString());
		assertThat(projectRepository.findById(id)).isEqualTo(new Project(id, name, description));
	}

	@Test
	@GUITest
	public void testAddProjectButton_GivenDuplicateId_ShowsErrorMessage() {
		// Arrange
		String id = "1";

		projectRepository.save(new Project(id, "Name", "Description"));

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id); // duplicate
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name 2");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description 2");

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(String.format(ErrorMessages.DUPLICATE_PROJECT, id));
		assertThat(projectRepository.findAll()).hasSize(1); // verify no new entries
	}

	@Test
	@GUITest
	public void testAddProjectButton_GivenNonNumericId_ShowsErrorMessage() {
		// Arrange
		String nonNumeric = "XYZ";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(nonNumeric);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(ErrorMessages.NON_NUMERICAL_ID);
		assertThat(projectRepository.findAll()).isEmpty(); // verify no new entries
	}

	@Test
	@GUITest
	public void testDeleteProjectButton_HappyPath_DeletesProject() {
		// Arrange
		String id = "1";

		GuiActionRunner.execute(() -> {
			projectController.addProject(id, "Name", "Description");
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
		assertThat(projectRepository.exists(id)).isFalse();
	}

	@Test
	@GUITest
	public void testDeleteProjectButton_WhenProjectHasAssociatedIssues_ShowsErrorMessage() {
		// Arrange
		String projectId = "1";
		String name = "Name";
		String description = "Description";

		GuiActionRunner.execute(() -> {
			projectController.addProject(projectId, name, description);
		});
		issueRepository.save(new Issue("1", "Name", "Description", "Priority", projectId));

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).containsExactly(new Project(projectId, name, description).toString());
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(ErrorMessages.PROJECT_HAS_ISSUES);
		assertThat(projectRepository.exists(projectId)).isTrue(); // verify not deleted
	}

	@Test
	@GUITest
	public void testIssueTab_ShowsAllIssuesOfAProjectToView() {
		// Arrange
		String projectId = "10";
		projectRepository.save(new Project(projectId, "Name", "Description"));

		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority", projectId);
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Priority", projectId);
		issueRepository.save(issue1);
		issueRepository.save(issue2);

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(issue1.toString(), issue2.toString());
	}

	@Test
	@GUITest
	public void testAddIssueButton_HappyPath_CreatesIssue() {
		// Arrange
		String projectId = "10";
		projectRepository.save(new Project(projectId, "Name", "Description"));

		String id = "1";
		String name = "Name";
		String description = "Description";
		String priority = "Low";

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id);
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(name);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(description);
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(new Issue(id, name, description, priority, projectId).toString());
		assertThat(issueRepository.findById(id)).isEqualTo(new Issue(id, name, description, priority, projectId));

	}

	@Test
	@GUITest
	public void testAddIssueButton_GivenDuplicateId_ShowsErrorMessage() {
		// Arrange
		String id = "1";
		String projectId = "10";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		Issue issue = new Issue(id, "Name", "Description", "Priority", projectId);
		issueRepository.save(issue);

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id); // duplicate
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(issue.toString());
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(String.format(ErrorMessages.DUPLICATE_ISSUE, id));
		assertThat(issueRepository.findAll()).hasSize(1); // verify no new entries
	}

	@Test
	@GUITest
	public void testAddIssueButton_GivenNonNumericId_ShowsErrorMessage() {
		// Arrange
		String projectId = "10";

		projectRepository.save(new Project(projectId, "Name", "Description"));

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("XYZ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(ErrorMessages.NON_NUMERICAL_ID);
		assertThat(issueRepository.findAll()).isEmpty(); // verify no new entries
	}

	@Test
	@GUITest
	public void testDeleteIssueButton_HappyPath_DeletesIssue() {
		// Arrange
		String projectId = "10";
		String id = "1";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		issueRepository.save(new Issue(id, "Name", "Description", "Priority", projectId));

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.list(ISSUE_LIST).selectItem(0);

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
		assertThat(issueRepository.exists(id)).isFalse();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenProjectHasNoIssues_ShowsEmptyListToView() {
		// Arrange
		String projectId = "1";
		projectRepository.save(new Project(projectId, "Name", "Description"));

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteProjectButton_SelectedProjectNotInDB_ShowsErrorMessageRefreshesList() {
		// Arrange
		String projectId = "1";
		String name = "Name";
		String description = "Description";

		GuiActionRunner.execute(() -> {
			projectController.addProject(projectId, name, description);
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		projectRepository.delete(projectId); // manually delete project -> stale view

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(ErrorMessages.PROJECT_DOESNT_EXIST);
	}

	@Test
	@GUITest
	public void testDeleteIssueButton_SelectedIssueNotInDB_ShowsErrorMessageRefreshesList() {
		// Arrange
		String projectId = "10";
		String id = "1";

		projectRepository.save(new Project(projectId, "Name", "Description"));
		issueRepository.save(new Issue(id, "Name", "Description", "Priority", projectId));

		GuiActionRunner.execute(() -> {
			projectController.listProjects();
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.list(ISSUE_LIST).selectItem(0);
		issueRepository.delete(id); // manually delete issue -> stale view

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(ErrorMessages.ISSUE_DOESNT_EXIST);
	}
}
