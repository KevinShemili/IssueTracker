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

import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

@RunWith(Parameterized.class)
public class ProjectControllerDeleteProjectParameterizedTest {

	@Parameters(name = "{index}: deleteProject(id={0}) ---> {3}")
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] {
						// Null | Empty ID
						{ null, ErrorMessages.NULL_EMPTY_ID },
						{ " ", ErrorMessages.NULL_EMPTY_ID },

						// Non numerical ID provided
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
	private ProjectController projectController;

	private AutoCloseable autoCloseable;

	private String id;
	private String errorMessage;

	public ProjectControllerDeleteProjectParameterizedTest(String id, String errorMessage) {
		this.id = id;
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
	public void testDeleteProject_WhenProvidedWithInvalidProjectId_ShowsCorrespondingErrorMessage() {
		// Act
		projectController.deleteProject(id);

		// Assert
		verify(issueTrackerView).showProjectError(errorMessage);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}
}
