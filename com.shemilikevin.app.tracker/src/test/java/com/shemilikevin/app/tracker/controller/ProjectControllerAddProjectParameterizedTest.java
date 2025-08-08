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

import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

@RunWith(Parameterized.class)
public class ProjectControllerAddProjectParameterizedTest {

	@Parameters(name = "{index}: addProject(id={0}, name={1}, description={2}) ---> {3}")
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] {
						// Null | Empty Project ID
						{ null, "Name", "Description", ErrorMessages.NULL_EMPTY_ID },
						{ " ", "Name", "Description", ErrorMessages.NULL_EMPTY_ID },

						// Null | Empty Project Name
						{ "1", null, "Description", ErrorMessages.NULL_EMPTY_NAME },
						{ "1", " ", "Description", ErrorMessages.NULL_EMPTY_NAME },

						// Null | Empty Project Description
						{ "1", "Name", null, ErrorMessages.NULL_EMPTY_DESCRIPTION },
						{ "1", "Name", " ", ErrorMessages.NULL_EMPTY_DESCRIPTION },

						// Non numerical ID provided - Note the "XYZ"
						{ "XYZ", "Name", "Description", ErrorMessages.NON_NUMERICAL_ID },
				});
	}

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private IssueTrackerView issueTrackerView;

	@InjectMocks
	private ProjectController projectController;

	private AutoCloseable autoCloseable;

	private String id;
	private String name;
	private String description;
	private String errorMessage;

	public ProjectControllerAddProjectParameterizedTest(String id, String name, String description,
			String errorMessage) {
		this.id = id;
		this.name = name;
		this.description = description;
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
	public void testAddProject_WhenProvidedWithInvalidInputs_ShowsCorrespondingErrorMessage() {
		// Act
		projectController.addProject(id, name, description);

		// Assert
		verify(issueTrackerView).showProjectError(errorMessage);
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}
}
