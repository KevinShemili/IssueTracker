package com.shemilikevin.app.tracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.shemilikevin.app.tracker.controller.IssueController;
import com.shemilikevin.app.tracker.controller.ProjectController;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;

@RunWith(GUITestRunner.class)
public class IssueTrackerSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final int TAB_PROJECTS = 0;
	private static final int TAB_ISSUES = 1;
	private static final String TABBED_PANE = "tabbedPane";
	private static final String PROJECT_ID_FIELD = "projectIdField";
	private static final String PROJECT_NAME_FIELD = "projectNameField";
	private static final String PROJECT_DESCRIPTION_FIELD = "projectDescriptionField";
	private static final String PROJECT_ADD_BUTTON = "addProjectButton";
	private static final String PROJECT_DELETE_BUTTON = "deleteProjectButton";
	private static final String PROJECT_LIST = "projectList";
	private static final String PROJECT_ERROR_LABEL = "projectErrorLabel";
	private static final String PROJECT_ID_LABEL = "projectIdLabel";
	private static final String PROJECT_NAME_LABEL = "projectNameLabel";
	private static final String PROJECT_DESCRIPTION_LABEL = "projectDescriptionLabel";
	private static final String ISSUE_ID_LABEL = "issueIdLabel";
	private static final String ISSUE_ID_FIELD = "issueIdField";
	private static final String ISSUE_NAME_LABEL = "issueNameLabel";
	private static final String ISSUE_NAME_FIELD = "issueNameField";
	private static final String ISSUE_DESCRIPTION_LABEL = "issueDescriptionLabel";
	private static final String ISSUE_DESCRIPTION_FIELD = "issueDescriptionField";
	private static final String ISSUE_PRIORITY_LABEL = "issuePriorityLabel";
	private static final String ISSUE_PRIORITY_COMBO = "issuePriorityComboBox";
	private static final String ISSUE_ERROR_LABEL = "issueErrorLabel";
	private static final String ISSUE_LIST = "issueList";
	private static final String ISSUE_ADD_BUTTON = "addIssueButton";
	private static final String ISSUE_DELETE_BUTTON = "deleteIssueButton";

	@Mock
	private ProjectController projectController;

	@Mock
	private IssueController issueController;

	private AutoCloseable autoCloseable;
	private FrameFixture frameFixture;
	private IssueTrackerSwingView issueTrackerView;

	@Override
	protected void onSetUp() throws Exception {
		autoCloseable = MockitoAnnotations.openMocks(this);

		GuiActionRunner.execute(() -> {
			issueTrackerView = new IssueTrackerSwingView();
			issueTrackerView.setProjectController(projectController);
			issueTrackerView.setIssueController(issueController);

			return issueTrackerView;
		});

		frameFixture = new FrameFixture(robot(), issueTrackerView);
		frameFixture.show();
	}

	@Override
	protected void onTearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	@GUITest
	public void testProjectTab_InitialState() {
		// Assert
		frameFixture.requireTitle("Issue Tracker");
		frameFixture.tabbedPane(TABBED_PANE).requireEnabled(Index.atIndex(TAB_PROJECTS));
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
		frameFixture.label(PROJECT_ID_LABEL);
		frameFixture.textBox(PROJECT_ID_FIELD).requireEnabled();
		frameFixture.label(PROJECT_NAME_LABEL);
		frameFixture.textBox(PROJECT_NAME_FIELD).requireEnabled();
		frameFixture.label(PROJECT_DESCRIPTION_LABEL);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireEnabled();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
		frameFixture.list(PROJECT_LIST);
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProvidedValidIdAndNameAndDescription_AddButtonIsEnabled() {
		// Act
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireEnabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenOneOrMoreFieldsAreEmpty_AddButtonRemainsDisabled() {
		// Act
		// Empty Description
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// Empty Name
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// Empty ID
		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// Empty ID + Name
		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// Empty ID + Description
		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// Empty NAme + Description
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		// Act
		// All Empty
		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		// Assert
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProjectIsSelectedFromList_DeleteButtonIsEnabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project("1", "Project Name", "Project Description"));
		});

		// Act
		frameFixture.list(PROJECT_LIST).selectItem(0);
		// Assert
		frameFixture.button(PROJECT_DELETE_BUTTON).requireEnabled();

		// Act
		frameFixture.list(PROJECT_LIST).clearSelection();
		// Assert
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProjectIsSelectedFromList_IssuesTabIsEnabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project("1", "Project Name", "Project Description"));
		});

		// Act
		frameFixture.list(PROJECT_LIST).selectItem(0);
		// Assert
		frameFixture.tabbedPane(TABBED_PANE).requireEnabled(Index.atIndex(TAB_ISSUES));

		// Act
		frameFixture.list(PROJECT_LIST).clearSelection();
		// Assert
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
	}

	@Test
	@GUITest
	public void testIssueTab_InitialState() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Assert
		frameFixture.label(ISSUE_ID_LABEL);
		frameFixture.textBox(ISSUE_ID_FIELD).requireEnabled();
		frameFixture.label(ISSUE_NAME_LABEL);
		frameFixture.textBox(ISSUE_NAME_FIELD).requireEnabled();
		frameFixture.label(ISSUE_DESCRIPTION_LABEL);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).requireEnabled();
		frameFixture.label(ISSUE_PRIORITY_LABEL);
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).requireEnabled();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
		frameFixture.list(ISSUE_LIST);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		frameFixture.button(ISSUE_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenProvidedValidIdAndNameAndDescriptionAndPriority_AddButtonIsEnabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Act
		// Valid Fields + Low Priority
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
		clearIssueInput();

		// Act
		// Valid Fields + Medium Priority
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(1);
		// ASsert
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
		clearIssueInput();

		// Act
		// Valid Fields + High Priority
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(2);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenOneOrMoreFieldsAreEmpty_AddButtonRemainsDisabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Act
		// Empty Fields
		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty ID + Name
		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty ID + Description
		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty ID
		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty Name + Description
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty Name
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty Description
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		// Act
		// Empty Priority
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).clearSelection();
		// Assert
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenIssueIsSelectedFromList_DeleteButtonIsEnabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.getIssueListModel()
					.addElement(new Issue("1", "Project Name", "Project Description", "Medium", "1"));
		});

		// Act
		frameFixture.list(ISSUE_LIST).selectItem(0);
		// Assert
		frameFixture.button(ISSUE_DELETE_BUTTON).requireEnabled();

		// Act
		frameFixture.list(ISSUE_LIST).clearSelection();
		// Assert
		frameFixture.button(ISSUE_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenProjecTabIsSelected_IssueTabBecomesDisabled() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS);

		// Assert
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
		frameFixture.textBox(PROJECT_ID_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_NAME_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireEmpty();
		frameFixture.list(PROJECT_LIST).requireNoSelection();
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenFieldsAreFilledAndIssueTabIsSelected_ReturningAgainToProjectTabClearedFields() {
		// Arrange
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectErrorLabel().setText("Some Error");
		});

		// Act
		// Project Tab -> Issue Tab -> Project Tab
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_PROJECTS);
		});

		// Assert
		frameFixture.textBox(PROJECT_ID_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_NAME_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireEmpty();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
		frameFixture.list(PROJECT_LIST).requireNoSelection();
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenFieldsAreFilledAndProjectTabIsSelected_ReturningAgainToIssueTabClearedFields() {

		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.getIssueErrorLabel().setText("Some Error");
		});

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);

		// Act
		// Issue Tab -> Project Tab -> Issue Tab
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_PROJECTS);
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Assert
		frameFixture.textBox(ISSUE_ID_FIELD).requireEmpty();
		frameFixture.textBox(ISSUE_NAME_FIELD).requireEmpty();
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).requireEmpty();
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).requireNoSelection();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
		frameFixture.list(ISSUE_LIST).requireNoSelection();
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		frameFixture.button(ISSUE_DELETE_BUTTON).requireDisabled();
	}

	@Test
	public void testShowProjects_WhenProvidedWithProjects_AddsAllProjectsToTheList() {
		// Arrange
		Project project1 = new Project("1", "Project1", "Description1");
		Project project2 = new Project("2", "Project2", "Description2");

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjects(Arrays.asList(project1, project2));
		});

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).containsExactly(project1.toString(), project2.toString());
	}

	@Test
	public void testShowProjects_WhenProvidedWithEmptyList_ShowsEmptyList() {
		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjects(Collections.emptyList());
		});

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
	}

	@Test
	public void testShowIssues_WhenProvidedWithIssues_AddsAllIssuesToTheList() {
		// Arrange
		Issue issue1 = new Issue("1", "Issue1", "Description1", "Low", "1");
		Issue issue2 = new Issue("2", "Issue2", "Description2", "Low", "1");

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.showIssues(Arrays.asList(issue1, issue2));
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(issue1.toString(), issue2.toString());
	}

	@Test
	public void testShowIssues_WhenProvidedWithEmptyList_ShowsEmptyList() {
		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.showIssues(Collections.emptyList());
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
	}

	@Test
	public void testShowProjectError_WhenProvidedWithMessage_ShowsMessageInProjectErrorLabel() {
		// Arrange
		String errorMessage = "Some Arbitrary Error.";

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjectError(errorMessage);
		});

		// Assert
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	public void testShowIssueError_WhenProvidedWithMessage_ShowsMessageInIssueErrorLabel() {
		// Arrange
		String errorMessage = "Some Arbitrary Error.";

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			issueTrackerView.showIssueError(errorMessage);
		});

		// Assert
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	public void testAddProjectButton_DelegatesToProjectController_AddsProjectAndClearsAnyError() {
		// Arrange
		String id = "1";
		String name = "Project Name";
		String description = "Project Description";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(name);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(description);
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectErrorLabel().setText("Some Error");
		});

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		verify(projectController).addProject(id, name, description);
		frameFixture.textBox(PROJECT_ID_FIELD).requireText("");
		frameFixture.textBox(PROJECT_NAME_FIELD).requireText("");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireText("");
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testDeleteProjectButton_DelegatesToProjectController_DeletesProjectAndClearsAnyError() {
		// Arrange
		String id = "1";
		Project project = new Project(id, "Project1", "Description1");

		GuiActionRunner.execute(() -> {
			DefaultListModel<Project> listModel = issueTrackerView.getProjectListModel();
			listModel.addElement(project);

			issueTrackerView.getProjectErrorLabel().setText("Some Error");
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		verify(projectController).deleteProject(id);
		frameFixture.list(PROJECT_LIST).requireNoSelection();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testIssueTab_DelegatesToIssueController_ListIssues() {
		// Arrange
		String id = "1";
		Project project = new Project(id, "Project Name", "Project Description");

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(project);
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		// Assert
		verify(issueController).listIssues(id);
	}

	@Test
	public void testProjectTab_DelegatesToProjectController_ListProjects() {
		// Arrange
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS);

		// Assert
		verify(projectController).listProjects();
	}

	@Test
	public void testAddIssueButton_DelegatesToIssueController_AddsIssueAndClearsAnyError() {
		// Arrange
		String id = "1";
		String name = "Issue Name";
		String description = "Issue Description";
		String projectId = "2";

		Project project = new Project(projectId, "Project Name", "Project Description");

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(project);
			issueTrackerView.getIssueErrorLabel().setText("Some Error");
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id);
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(name);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(description);
		frameFixture.comboBox().selectItem(0);

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		verify(issueController).addIssue(id, name, description, "Low", projectId);
		frameFixture.textBox(ISSUE_ID_FIELD).requireText("");
		frameFixture.textBox(ISSUE_NAME_FIELD).requireText("");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).requireText("");
		frameFixture.comboBox().requireNoSelection();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testDeleteIssueButton_DelegatesToIssueController_DeletesIssueAndClearsAnyError() {
		// Arrange
		String issueId = "1";
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
			DefaultListModel<Issue> listModel = issueTrackerView.getIssueListModel();
			listModel.addElement(new Issue(issueId, "Issue Name", "Issue Description", "Low", "1"));
			issueTrackerView.getIssueErrorLabel().setText("Some Error");
		});

		frameFixture.list(ISSUE_LIST).selectItem(0);

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		verify(issueController).deleteIssue(issueId);
		frameFixture.list(ISSUE_LIST).requireNoSelection();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
	}

	private void clearProjectInput() {
		frameFixture.textBox(PROJECT_ID_FIELD).setText("");
		frameFixture.textBox(PROJECT_NAME_FIELD).setText("");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).setText("");
		frameFixture.list(PROJECT_LIST).clearSelection();
	}

	private void clearIssueInput() {
		frameFixture.textBox(ISSUE_ID_FIELD).setText("");
		frameFixture.textBox(ISSUE_NAME_FIELD).setText("");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).setText("");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).clearSelection();
		frameFixture.list(ISSUE_LIST).clearSelection();
	}
}
