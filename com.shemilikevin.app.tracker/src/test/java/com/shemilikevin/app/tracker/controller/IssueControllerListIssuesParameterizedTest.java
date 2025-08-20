package com.shemilikevin.app.tracker.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.shemilikevin.app.tracker.helpers.ErrorMessages;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

@RunWith(Parameterized.class)
public class IssueControllerListIssuesParameterizedTest {

	@Parameters(name = "{index}: listIssues(projectId={0}) ---> {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] {
						// Null | Empty Project ID
						{ null, ErrorMessages.NULL_EMPTY_ID },
						{ " ", ErrorMessages.NULL_EMPTY_ID },

						// Non numerical Project ID
						{ "XYZ", ErrorMessages.NON_NUMERICAL_ID },
				});
	}

	@Mock
	private IssueRepository issueRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private IssueTrackerView issueTrackerView;

	@InjectMocks
	private IssueController issueController;

	private AutoCloseable autoCloseable;

	private String projectId;
	private String errorMessage;

	public IssueControllerListIssuesParameterizedTest(String projectId, String errorMessage) {
		this.projectId = projectId;
		this.errorMessage = errorMessage;
	}

	@Before
	public void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		autoCloseable.close();
	}

	@Test
	public void testListIssues_WhenProvidedWithInvalidProjectId_ShowsCorrespondingErrorMessage() {
		// Act
		issueController.listIssues(projectId);

		// Assert
		verify(issueTrackerView).showIssueError(errorMessage);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}
}
