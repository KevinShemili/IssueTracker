package com.shemilikevin.app.tracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RunWith(GUITestRunner.class)
public class IssueTrackerSwingAppE2E extends AssertJSwingJUnitTestCase { // NOSONAR we want this naming convention

	@ClassRule
	public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:5");

	private static final String DATABASE_NAME = "database";
	private static final String PROJECT_COLLECTION = "project";
	private static final String ISSUE_COLLECTION = "issue";

	private static final String TABBED_PANE = "tabbedPane";
	private static final String PROJECT_ID_FIELD = "projectIdField";
	private static final String PROJECT_NAME_FIELD = "projectNameField";
	private static final String PROJECT_DESCRIPTION_FIELD = "projectDescriptionField";
	private static final String PROJECT_ADD_BUTTON = "addProjectButton";
	private static final String PROJECT_DELETE_BUTTON = "deleteProjectButton";
	private static final String PROJECT_LIST = "projectList";
	private static final String PROJECT_ERROR_LABEL = "projectErrorLabel";
	private static final String ISSUE_ERROR_LABEL = "issueErrorLabel";
	private static final String ISSUE_ID_FIELD = "issueIdField";
	private static final String ISSUE_NAME_FIELD = "issueNameField";
	private static final String ISSUE_DESCRIPTION_FIELD = "issueDescriptionField";
	private static final String ISSUE_PRIORITY_COMBO = "issuePriorityComboBox";
	private static final String ISSUE_LIST = "issueList";
	private static final String ISSUE_ADD_BUTTON = "addIssueButton";
	private static final String ISSUE_DELETE_BUTTON = "deleteIssueButton";

	private static final String PROJECT_FIXTURE_1_ID = "1";
	private static final String PROJECT_FIXTURE_1_NAME = "Desktop Application";
	private static final String PROJECT_FIXTURE_1_DESCRIPTION = "POS Application";
	private static final String PROJECT_FIXTURE_2_ID = "2";
	private static final String PROJECT_FIXTURE_2_NAME = "Web Application";
	private static final String PROJECT_FIXTURE_2_DESCRIPTION = "Online Shop";

	private static final String ISSUE_FIXTURE_1_ID = "1";
	private static final String ISSUE_FIXTURE_1_NAME = "Button Error";
	private static final String ISSUE_FIXTURE_1_DESCRIPTION = "When button is clicked it...";
	private static final String ISSUE_FIXTURE_PRIORITY = "Low";
	private static final String ISSUE_FIXTURE_2_ID = "2";
	private static final String ISSUE_FIXTURE_2_NAME = "Slow Query";
	private static final String ISSUE_FIXTURE_2_DESCRIPTION = "Loading is very slow when you open...";

	private MongoClient mongoClient;
	private FrameFixture frameFixture;

	@Override
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongoContainer.getHost();
		Integer mappedPort = mongoContainer.getFirstMappedPort();

		mongoClient = new MongoClient(new ServerAddress(containerIpAddress, mappedPort));

		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		database.drop();

		seedData(database);

		application("com.shemilikevin.app.tracker.app.swing.IssueTrackerSwingApp")
				.withArgs(
						"--mongo-host=" + containerIpAddress,
						"--mongo-port=" + mappedPort.toString())
				.start();

		frameFixture = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Issue Tracker".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() throws Exception {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testProjectTab_OnStart_ShowsAllDatabaseProjects() {
		// Assert
		assertThat(frameFixture.list(PROJECT_LIST).contents())
				.anySatisfy(e -> assertThat(e).contains(PROJECT_FIXTURE_1_ID, PROJECT_FIXTURE_1_NAME,
						PROJECT_FIXTURE_1_DESCRIPTION))
				.anySatisfy(e -> assertThat(e).contains(PROJECT_FIXTURE_2_ID, PROJECT_FIXTURE_2_NAME,
						PROJECT_FIXTURE_2_DESCRIPTION));
	}

	@Test
	@GUITest
	public void testAddProjectButton_CreatesNewProject() {
		// Arrange
		String id = "3"; // Last id is 2
		String name = "Name";
		String description = "Description";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(name);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(description);

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		assertThat(frameFixture.list(PROJECT_LIST).contents())
				.anySatisfy(e -> assertThat(e).contains(id, name, description));
	}

	@Test
	@GUITest
	public void testAddProjectButton_ProvidedWithNonNumericId_ShowsErrorMessage() {
		// Arrange
		String id = "XYZ";
		String name = "Name";
		String description = "Description";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(name);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(description);

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		assertThat(frameFixture.label(PROJECT_ERROR_LABEL).text())
				.matches(Pattern.compile(".*" + "must be numerical" + ".*"));
	}

	@Test
	@GUITest
	public void testDeleteProjectButton_DeletesProject() {
		// Arrange
		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_2_NAME + ".*"));

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		assertThat(frameFixture.list(PROJECT_LIST).contents()).noneMatch(e -> e.contains(PROJECT_FIXTURE_2_NAME));
	}

	@Test
	@GUITest
	public void testDeleteProjectButton_ProjectHasIssues_ShowsErrorMessage() {
		// Arrange
		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_1_NAME + ".*"));

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		assertThat(frameFixture.label(PROJECT_ERROR_LABEL).text())
				.matches(Pattern.compile(".*" + "has associated issues" + ".*"));
	}

	@Test
	@GUITest
	public void testIssueTab_SelectingProjectWithIssues_ShowsAllIssuesOfProject() {
		// Arrange
		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_1_NAME + ".*"));

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(Pattern.compile(".*" + "Issue" + ".*"));

		// Assert
		assertThat(frameFixture.list(ISSUE_LIST).contents())
				.anySatisfy(e -> assertThat(e).contains(ISSUE_FIXTURE_1_ID, ISSUE_FIXTURE_1_NAME,
						ISSUE_FIXTURE_1_DESCRIPTION, ISSUE_FIXTURE_PRIORITY, PROJECT_FIXTURE_1_ID))
				.anySatisfy(e -> assertThat(e).contains(ISSUE_FIXTURE_2_ID, ISSUE_FIXTURE_2_NAME,
						ISSUE_FIXTURE_2_DESCRIPTION, ISSUE_FIXTURE_PRIORITY, PROJECT_FIXTURE_1_ID));
	}

	@Test
	@GUITest
	public void testIssueTab_SelectingProjectWithoutIssues_ShowsEmptyList() {
		// Arrange
		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_2_NAME + ".*"));

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(Pattern.compile(".*" + "Issue" + ".*"));

		// Assert
		assertThat(frameFixture.list(ISSUE_LIST).contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testAddIssueButton_CreatesNewIssue() {
		// Arrange
		String id = "3"; // Last id is 2
		String name = "Name";
		String description = "Description";
		String priority = "Low";

		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_2_NAME + ".*"));
		frameFixture.tabbedPane(TABBED_PANE).selectTab(Pattern.compile(".*" + "Issue" + ".*"));

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id);
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(name);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(description);
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(Pattern.compile(".*" + priority + ".*"));

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		assertThat(frameFixture.list(ISSUE_LIST).contents())
				.anySatisfy(e -> assertThat(e).contains(id, name, description, priority, PROJECT_FIXTURE_2_ID));
	}

	@Test
	@GUITest
	public void testAddIssueButton_ProvidedWithNonNumericId_ShowsErrorMessage() {
		// Arrange
		String id = "XYZ";
		String name = "Name";
		String description = "Description";
		String priority = "Low";

		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_2_NAME + ".*"));
		frameFixture.tabbedPane(TABBED_PANE).selectTab(Pattern.compile(".*" + "Issue" + ".*"));

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id);
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(name);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(description);
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(Pattern.compile(".*" + priority + ".*"));

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		assertThat(frameFixture.label(ISSUE_ERROR_LABEL).text())
				.matches(Pattern.compile(".*" + "must be numerical" + ".*"));
	}

	@Test
	@GUITest
	public void testDeleteIssueButton_DeletesIssue() {
		// Arrange
		frameFixture.list(PROJECT_LIST).selectItem(Pattern.compile(".*" + PROJECT_FIXTURE_1_NAME + ".*"));
		frameFixture.tabbedPane(TABBED_PANE).selectTab(Pattern.compile(".*" + "Issue" + ".*"));
		frameFixture.list(ISSUE_LIST).selectItem(Pattern.compile(".*" + ISSUE_FIXTURE_1_NAME + ".*"));

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		assertThat(frameFixture.list(ISSUE_LIST).contents()).noneMatch(e -> e.contains(ISSUE_FIXTURE_1_NAME));
	}

	private void seedData(MongoDatabase database) {
		MongoCollection<Document> projectCollection = database.getCollection(PROJECT_COLLECTION);
		MongoCollection<Document> issueCollection = database.getCollection(ISSUE_COLLECTION);

		// Project 1: Has 2 Issues
		projectCollection.insertOne(new Document()
				.append("id", PROJECT_FIXTURE_1_ID)
				.append("name", PROJECT_FIXTURE_1_NAME)
				.append("description", PROJECT_FIXTURE_1_DESCRIPTION));

		issueCollection.insertOne(new Document()
				.append("id", ISSUE_FIXTURE_1_ID)
				.append("name", ISSUE_FIXTURE_1_NAME)
				.append("description", ISSUE_FIXTURE_1_DESCRIPTION)
				.append("priority", ISSUE_FIXTURE_PRIORITY)
				.append("projectId", PROJECT_FIXTURE_1_ID)); // Reference to Project 1

		issueCollection.insertOne(new Document()
				.append("id", ISSUE_FIXTURE_2_ID)
				.append("name", ISSUE_FIXTURE_2_NAME)
				.append("description", ISSUE_FIXTURE_2_DESCRIPTION)
				.append("priority", ISSUE_FIXTURE_PRIORITY)
				.append("projectId", PROJECT_FIXTURE_1_ID)); // Reference to Project 1

		// Project 2: Has 0 Issues
		projectCollection.insertOne(new Document()
				.append("id", PROJECT_FIXTURE_2_ID)
				.append("name", PROJECT_FIXTURE_2_NAME)
				.append("description", PROJECT_FIXTURE_2_DESCRIPTION));
	}
}
