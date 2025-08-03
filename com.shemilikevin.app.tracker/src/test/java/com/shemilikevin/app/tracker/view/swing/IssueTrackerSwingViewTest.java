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
		frameFixture.tabbedPane(TABBED_PANE).selectTab("Projects");
		frameFixture.tabbedPane(TABBED_PANE).requireDisabled(Index.atIndex(TAB_ISSUES));

		frameFixture.label("projectIdLabel");
		frameFixture.textBox("projectIdField").requireEnabled();
		frameFixture.label("projectNameLabel");
		frameFixture.textBox("projectNameField").requireEnabled();
		frameFixture.label("projectDescriptionLabel");
		frameFixture.textBox("projectDescriptionField").requireEnabled();
		frameFixture.label("errorLabel").requireText(" ");
		frameFixture.list("projectList");
		frameFixture.button("addProjectButton").requireDisabled();
		frameFixture.button("deleteProjectButton").requireDisabled();
	}
}
