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
public class IssueControllerAddIssueParameterizedTest {

	@Parameters(name = "{index}: addIssue(issueId={0}, name={1}, description={2}, priority={3}, projectId={4}) ---> {5}")
	public static Collection<Object[]> data() {
		return Arrays.asList(
				new Object[][] {
						// Null | Empty Issue ID
						{ null, "Name", "Description", "Low", "10", ErrorMessages.NULL_EMPTY_ID },
						{ " ", "Name", "Description", "Low", "10", ErrorMessages.NULL_EMPTY_ID },

						// Null | Empty Issue Name
						{ "1", null, "Description", "Low", "10", ErrorMessages.NULL_EMPTY_NAME },
						{ "1", " ", "Description", "Low", "10", ErrorMessages.NULL_EMPTY_NAME },

						// Null | Empty Issue Description
						{ "1", "Name", null, "Low", "10", ErrorMessages.NULL_EMPTY_DESCRIPTION },
						{ "1", "Name", " ", "Low", "10", ErrorMessages.NULL_EMPTY_DESCRIPTION },

						// Null | Empty | Not Allowed Issue Priority
						{ "1", "Name", "Description", null, "10", ErrorMessages.NULL_EMPTY_PRIORITY },
						{ "1", "Name", "Description", " ", "10", ErrorMessages.NULL_EMPTY_PRIORITY },
						{ "1", "Name", "Description", "Some Unexpected Priority", "10",
								ErrorMessages.NOT_ALLOWED_PRIORITY },

						// Null | Empty Project ID
						{ "1", "Name", "Description", "Low", null, ErrorMessages.NULL_EMPTY_ID },
						{ "1", "Name", "Description", "Low", " ", ErrorMessages.NULL_EMPTY_ID },

						// Non numerical IDs provided - Note the "XYZ"
						{ "XYZ", "Name", "Description", "Low", "10", ErrorMessages.NON_NUMERICAL_ID },
						{ "1", "Name", "Description", "Low", "XYZ", ErrorMessages.NON_NUMERICAL_ID },
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

	private String id;
	private String name;
	private String description;
	private String priority;
	private String projectId;
	private String errorMessage;

	public IssueControllerAddIssueParameterizedTest(String id, String name, String description, String priority,
			String projectId, String errorMessage) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
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
	public void testAddIssue_WhenProvidedWithInvalidInputs_ShowsCorrespondingErrorMessage() {
		// Act
		issueController.addIssue(id, name, description, priority, projectId);

		// Assert
		verify(issueTrackerView).showIssueError(errorMessage);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}
}
