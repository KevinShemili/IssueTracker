package com.shemilikevin.app.tracker.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class IssueTrackerSwingViewTest extends AssertJSwingJUnitTestCase {

	private static final int TAB_PROJECTS = 0;
	private static final int TAB_ISSUES = 1;
	private static final String TABBED_PANE = "tabbedPane";
	private static final String PROJECT_ID_FIELD = "projectIdField";
	private static final String PROJECT_NAME_FIELD = "projectNameField";
	private static final String PROJECT_DESCRIPTION_FIELD = "projectDescriptionField";
	private static final String ADD_BUTTON = "addProjectButton";

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
		frameFixture.list("projectList");
		frameFixture.button(ADD_BUTTON).requireDisabled();
		frameFixture.button("deleteProjectButton").requireDisabled();
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
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText("Description");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText("Name");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");

		frameFixture.button(ADD_BUTTON).requireDisabled();
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText("1");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");
		cleanFields();

		frameFixture.textBox(PROJECT_ID_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_NAME_FIELD).enterText(" ");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).enterText(" ");
	}

	private void cleanFields() {
		frameFixture.textBox(PROJECT_ID_FIELD).setText("");
		frameFixture.textBox(PROJECT_NAME_FIELD).setText("");
		frameFixture.textBox(PROJECT_DESCRIPTION_FIELD).setText("");
	}
}
