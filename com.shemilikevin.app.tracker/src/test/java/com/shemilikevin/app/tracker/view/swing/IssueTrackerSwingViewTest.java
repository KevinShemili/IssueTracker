package com.shemilikevin.app.tracker.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

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

	/*
	 * SECTION 1 - Validate the GUI
	 */
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
		addProjectToList();

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
		addProjectToList();

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
		goToIssueTab();

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
		goToIssueTab();

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
		goToIssueTab();

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
	public void testIssueTab_IssueSelectionFromList_EnablesOrDisablesTheDeleteButton() {
		// Arrange
		goToIssueTab();
		addIssueToList();

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
	public void testIssueTab_WhenProjectTabIsSelected_IssueTabBecomesDisabled() {
		// Arrange
		// Start from Issue Tab
		goToIssueTab();

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS); // Go to Projects Tab

		// Assert
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
	}

	@Test
	@GUITest
	public void testProjectTab_FillFieldsAndSelectIssueTab_ReturningToProjectTabFieldsAreEmpty() {
		// Arrange
		// Start from Project Tab & Fill Fields
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");
		fillProjectErrorLabel();

		goToIssueTab();

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS); // Come back again to Projects Tab

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
	public void testIssueTab_FillFieldsAndSelectProjectTab_ReturningToIssueTabFieldsAreEmpty() {
		// Arrange
		// Start from Issue Tab
		goToIssueTab();

		// Fill the Fields
		frameFixture.textBox(ISSUE_ID_FIELD).enterText("1");
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText("Name");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText("Description");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0);
		fillIssueErrorLabel();

		goToProjectTab();
		frameFixture.list(PROJECT_LIST).selectItem(0); // Activate Issues Tab

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES); // Navigate again to Issues Tab

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

	/*
	 * SECTION 2 - Validate the Methods
	 */
	@Test
	public void testShowProjects_WhenProvidedWithProjects_AddsAllProjectsToTheListAndClearsAnyError() {
		// Arrange
		Project project1 = new Project("1", "Name 1", "Description 1");
		Project project2 = new Project("2", "Name 2", "Description 2");
		fillProjectErrorLabel();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjects(Arrays.asList(project1, project2));
		});

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).containsExactly(project1.toString(), project2.toString());
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testShowProjects_WhenProvidedWithEmptyList_ShowsEmptyListAndClearsAnyError() {
		// Arrange
		fillProjectErrorLabel();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showProjects(Collections.emptyList());
		});

		// Assert
		String[] listContents = frameFixture.list(PROJECT_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(PROJECT_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testShowIssues_WhenProvidedWithIssues_AddsAllIssuesToTheListAndClearsAnyError() {
		// Arrange
		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority 1", "10");
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Priority 2", "10");
		goToIssueTab();
		fillIssueErrorLabel();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssues(Arrays.asList(issue1, issue2));
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).containsExactly(issue1.toString(), issue2.toString());
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testShowIssues_WhenProvidedWithEmptyList_ShowsEmptyListAndClearsAnyError() {
		// Arrange
		goToIssueTab();
		fillIssueErrorLabel();

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssues(Collections.emptyList());
		});

		// Assert
		String[] listContents = frameFixture.list(ISSUE_LIST).contents();
		assertThat(listContents).isEmpty();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testShowProjectError_WhenProvidedWithMessage_ShowsMessageInProjectErrorLabel() {
		// Arrange
		String errorMessage = "Some Error";

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
		goToIssueTab();
		String errorMessage = "Some Error";

		// Act
		GuiActionRunner.execute(() -> {
			issueTrackerView.showIssueError(errorMessage);
		});

		// Assert
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(errorMessage);
	}

	/*
	 * SECTION 3 - Validate GUI correctly calling the Controller
	 */
	@Test
	public void testAddProjectButton_DelegatesToProjectController_AddsProjectAndClearsFields() {
		// Arrange
		String id = "1";
		String name = "Name";
		String description = "Description";

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
	public void testDeleteProjectButton_DelegatesToProjectController_DeletesProjectAndClearsSelection() {
		// Arrange
		String id = "1";
		addProjectToList(id);

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.button(PROJECT_DELETE_BUTTON).click();

		// Assert
		verify(projectController).deleteProject(id);
		frameFixture.list(PROJECT_LIST).requireNoSelection();
	}

	@Test
	public void testIssueTab_DelegatesToIssueController_ListIssues() {
		// Arrange
		String id = "1";
		addProjectToList(id);

		frameFixture.list(PROJECT_LIST).selectItem(0);

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_ISSUES);

		// Assert
		verify(issueController).listIssues(id);
	}

	@Test
	public void testProjectTab_DelegatesToProjectController_ListProjects() {
		// Arrange
		goToIssueTab();

		// Act
		frameFixture.tabbedPane(TABBED_PANE).selectTab(TAB_PROJECTS);

		// Assert
		verify(projectController).listProjects();
	}

	@Test
	public void testAddIssueButton_DelegatesToIssueController_AddsIssueAndClearsFields() {
		// Arrange
		String id = "1";
		String name = "Name";
		String description = "Description";
		String projectId = "10";
		goToIssueTab(projectId);

		frameFixture.textBox(ISSUE_ID_FIELD).enterText(id);
		frameFixture.textBox(ISSUE_NAME_FIELD).enterText(name);
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).enterText(description);
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).selectItem(0); // Low

		// Act
		frameFixture.button(ISSUE_ADD_BUTTON).click();

		// Assert
		verify(issueController).addIssue(id, name, description, "Low", projectId);
		frameFixture.textBox(ISSUE_ID_FIELD).requireText("");
		frameFixture.textBox(ISSUE_NAME_FIELD).requireText("");
		frameFixture.textBox(ISSUE_DESCRIPTION_FIELD).requireText("");
		frameFixture.comboBox(ISSUE_PRIORITY_COMBO).requireNoSelection();
		frameFixture.label(ISSUE_ERROR_LABEL).requireText(" ");
	}

	@Test
	public void testDeleteIssueButton_DelegatesToIssueController_DeletesIssueAndClearsSelection() {
		// Arrange
		String issueId = "1";
		goToIssueTab();
		addIssueToList(issueId);

		frameFixture.list(ISSUE_LIST).selectItem(0);

		// Act
		frameFixture.button(ISSUE_DELETE_BUTTON).click();

		// Assert
		verify(issueController).deleteIssue(issueId);
		frameFixture.list(ISSUE_LIST).requireNoSelection();
	}

	/*
	 * Some Helper Methods
	 */
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

	private void goToIssueTab() {
		goToIssueTab("1");
	}

	private void goToIssueTab(String projectReferenceId) {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project(projectReferenceId, "Name", "Description"));
			issueTrackerView.getProjectJList().setSelectedIndex(0);
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_ISSUES);
		});
	}

	private void goToProjectTab() {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getTabbedPane().setSelectedIndex(TAB_PROJECTS);
		});
	}

	private void addProjectToList(String id) {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectListModel().addElement(new Project(id, "Name", "Description"));
		});
	}

	private void addIssueToList(String id) {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getIssueListModel().addElement(new Issue(id, "Name", "Description", "Priority", "1"));
		});
	}

	private void addProjectToList() {
		addProjectToList("1");
	}

	private void addIssueToList() {
		addIssueToList("1");
	}

	private void fillProjectErrorLabel() {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getProjectErrorLabel().setText("Some Error");
		});
	}

	private void fillIssueErrorLabel() {
		GuiActionRunner.execute(() -> {
			issueTrackerView.getIssueErrorLabel().setText("Some Error");
		});
	}
}
