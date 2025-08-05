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

	private FrameFixture frameFixture;
	private IssueTrackerSwingView issueTrackerView;
	private AutoCloseable autoCloseable;

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
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(PROJECT_ADD_BUTTON).requireEnabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenOneOrMoreFieldsAreEmpty_AddButtonRemainsDisabled() {

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");
		clearProjectInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProjectIsSelectedFromList_DeleteButtonIsEnabled() {

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project("1", "Project Name", "Project Description"));
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.button(PROJECT_DELETE_BUTTON).requireEnabled();

		clearProjectInput();
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProjectIsSelectedFromList_IssuesTabIsEnabled() {

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project("1", "Project Name", "Project Description"));
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).requireEnabled(Index.atIndex(TAB_ISSUES));

		clearProjectInput();
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
	}

	@Test
	@GUITest
	public void testIssueTab_InitialState() {

		switchToIssueTab();

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

		switchToIssueTab();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(1);
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(2);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.button(ISSUE_ADD_BUTTON).requireEnabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenOneOrMoreFieldsAreEmpty_AddButtonRemainsDisabled() {

		switchToIssueTab();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(" ");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(" ");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
		clearIssueInput();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).clearSelection();
		frameFixture.button(ISSUE_ADD_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenIssueIsSelectedFromList_DeleteButtonIsEnabled() {

		switchToIssueTab();

		GuiActionRunner.execute(() -> {
			issueTrackerView.getIssueListModel()
					.addElement(new Issue("1", "Project Name", "Project Description", "Medium", "1"));
		});

		frameFixture.list(ISSUE_LIST).selectItem(0);
		frameFixture.button(ISSUE_DELETE_BUTTON).requireEnabled();

		clearIssueInput();
		frameFixture.button(ISSUE_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenProjecTabIsSelected_IssueTabBecomesDisabled() {

		switchToIssueTab();

		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS);

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
	public void testProjectTab_WhenFieldsAreFilledAndIssueTabIsSelected_ReturningAgainToProjectTabSeeClearFields() {

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		switchToIssueTab();
		switchBackToProjectTab();

		frameFixture.textBox(PROJECT_ID_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_NAME_FIELD).requireEmpty();
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireEmpty();
		frameFixture.list(PROJECT_LIST).requireNoSelection();
		frameFixture.button(PROJECT_ADD_BUTTON).requireDisabled();
		frameFixture.button(PROJECT_DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testIssueTab_WhenFieldsAreFilledAndProjectTabIsSelected_ReturningAgainToIssueTabSeeClearFields() {

		switchToIssueTab();

		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);

		switchBackToProjectTab();
		switchBackToIssueTab();

		frameFixture.textBox(ISSUE_ID_FIELD).requireEmpty();
		frameFixture.textBox(ISSUE_NAME_FIELD).requireEmpty();
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).requireEmpty();
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).requireNoSelection();
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
		switchToIssueTab();
		Issue issue1 = new Issue("1", "Issue1", "Description1", "Low", "1");
		Issue issue2 = new Issue("2", "Issue2", "Description2", "Low", "1");

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssues(Arrays.asList(issue1, issue2));
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(issue1.toString(), issue2.toString());
	}

	@Test
	public void testShowIssues_WhenProvidedWithEmptyList_ShowsEmptyList() {
		// Arrange
		switchToIssueTab();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssues(Collections.emptyList());
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
	}

	@Test
	public void testShowProjectError_WhenProvidedWithMessage_ShowsMessageInErrorLabel() {
		// Arrange
		String errorMessage = "Some arbitrary error.";

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjectError(errorMessage);
		});

		// Assert
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	public void testShowIssueError_WhenProvidedWithMessage_ShowsMessageInErrorLabel() {
		// Arrange
		String errorMessage = "Some arbitrary error.";
		switchToIssueTab();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssueError(errorMessage);
		});

		// Assert
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(errorMessage);
	}

	@Test
	public void testAddProjectButton_DelegatesToProjectControllerAddProject() {
		// Arrange
		String id = "1";
		String name = "Project Name";
		String description = "Project Description";

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(id);
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(name);
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(description);

		// Act
		frameFixture.button(PROJECT_ADD_BUTTON).click();

		// Assert
		verify(projectController).addProject(id, name, description);
		frameFixture.textBox(PROJECT_ID_FIELD).requireText("");
		frameFixture.textBox(PROJECT_NAME_FIELD).requireText("");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireText("");
	}

	@Test
	public void testDeleteProjectButton_DelegatesToProjectControllerDeleteProject() {
		// Arrange
		Project project1 = new Project("1", "Project1", "Description1");
		Project project2 = new Project("2", "Project2", "Description2");

		GuiActionRunner.execute(() -> {
			DefaultListModel<Project> listModel = issueTrackerView.getProjectListModel();
			listModel.addElement(project1);
			listModel.addElement(project2);
		});

		frameFixture.list(PROJECT_LIST).selectItem(1);

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		verify(projectController).deleteProject("2");
		frameFixture.list(PROJECT_LIST).requireNoSelection();
	}

	@Test
	public void testIssueTab_DelegatesToIssueControllerListIssues() {
		// Arrange
		Project project = new Project("1", "Project Name", "Project Description");

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(project);
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		// Assert
		verify(issueController).listIssues("1");
	}

	@Test
	public void testAddIssueButton_DelegatesToIssueControllerAddIssue() {
		// Arrange
		String id = "1";
		String name = "Issue Name";
		String description = "Issue Description";
		String projectId = "2";

		Project project = new Project(projectId, "Project Name", "Project Description");

		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(project);
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
	}

	@Test
	public void testDeleteIssueButton_DelegatesToIssueControllerDeleteIssue() {
		// Arrange
		String issueId = "1";
		switchToIssueTab();

		GuiActionRunner.execute(() -> {
			DefaultListModel<Issue> listModel = issueTrackerView.getIssueListModel();
			listModel.addElement(new Issue(issueId, "Issue Name", "Issue Description", "Low", "1"));
		});

		frameFixture.list(ISSUE_LIST).selectItem(0);

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		verify(issueController).deleteIssue(issueId);
		frameFixture.list(ISSUE_LIST).requireNoSelection();
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

	private void switchToIssueTab() {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project("1", "Project Name", "Project Description"));
		});

		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);
	}

	private void switchBackToProjectTab() {
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS);
	}

	private void switchBackToIssueTab() {
		frameFixture.list(PROJECT_LIST).selectItem(0);
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);
	}
}
