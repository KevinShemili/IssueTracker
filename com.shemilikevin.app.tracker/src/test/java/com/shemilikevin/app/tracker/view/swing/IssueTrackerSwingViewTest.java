package com.shemilikevin.app.tracker.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.shemilikevin.app.tracker.model.Project;

@RunWith(GUITestRunner.class)
public class IssueTrackerSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final int TAB_PROJECTS = 0;
	private static final int TAB_ISSUES = 1;
	private static final String TABBED_PANE = "tabbedPane";
	private static final String PROJECT_ID_FIELD = "projectIdField";
	private static final String PROJECT_NAME_FIELD = "projectNameField";
	private static final String PROJECT_DESCRIPTION_FIELD = "projectDescriptionField";
	private static final String ADD_BUTTON = "addProjectButton";
	private static final String DELETE_BUTTON = "deleteProjectButton";
	private static final String PROJECT_LIST = "projectList";

	private FrameFixture frameFixture;
	private IssueTrackerSwingView issueTrackerView;

	@Override
	protected void onSetUp() throws Exception {
		GuiActionRunner.execute(() -> {
			issueTrackerView = new IssueTrackerSwingView();

			return issueTrackerView;
		});

		frameFixture = new FrameFixture(robot(), issueTrackerView);
		frameFixture.show();
	}

	@Test
	@GUITest
	public void testProjectTab_InitialState() {

		frameFixture.tabbedPane(TABBED_PANE).requireEnabled(Index.atIndex(TAB_PROJECTS));
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));
		frameFixture.label("projectIdLabel");
		frameFixture.textBox(PROJECT_ID_FIELD).requireEnabled();
		frameFixture.label("projectNameLabel");
		frameFixture.textBox(PROJECT_NAME_FIELD).requireEnabled();
		frameFixture.label("projectDescriptionLabel");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).requireEnabled();
		frameFixture.label("errorLabel").requireText(" ");
		frameFixture.list(PROJECT_LIST);
		frameFixture.button(ADD_BUTTON).requireDisabled();
		frameFixture.button(DELETE_BUTTON).requireDisabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenProvidedValidIdAndNameAndDescription_AddButtonIsEnabled() {
		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireEnabled();
	}

	@Test
	@GUITest
	public void testProjectTab_WhenOneOrMoreFieldsAreEmpty_AddButtonRemainsDisabled() {

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		clearInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		clearInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		clearInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		clearInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		clearInput();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");
		clearInput();

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
		frameFixture.button(DELETE_BUTTON).requireEnabled();

		clearInput();
		frameFixture.button(DELETE_BUTTON).requireDisabled();
	}

	private void clearInput() {
		frameFixture.textBox(PROJECT_ID_FIELD).setText("");
		frameFixture.textBox(PROJECT_NAME_FIELD).setText("");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).setText("");
		frameFixture.list(PROJECT_LIST).clearSelection();
	}
}
