package com.shemilikevin.app.tracker.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueControllerTest {

	private static final String NON_NUMERIC_ID = "XX";
	private static final String EMPTY_STRING = " ";
	private static final String ISSUE_NAME = "Broken Button";
	private static final String ISSUE_DESCRIPTION = "Button is not clickable when...";
	private static final String ISSUE_PRIORITY = "Low";
	private static final String PROJECT_ID = "2";
	private static final String ISSUE_ID = "1";

	@Mock
	private IssueRepository issueRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private IssueTrackerView issueTrackerView;

	@InjectMocks
	private IssueController issueController;

	private AutoCloseable autoCloseable;

	@Before
	public void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		autoCloseable.close();
	}

	@Test
	public void testListIssues_WhenProjectHasIssues_ShowsAllIssues() {
		// Arrange
		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		when(projectRepository.exists(PROJECT_ID)).thenReturn(true);
		when(issueRepository.findByProjectId(PROJECT_ID)).thenReturn(Arrays.asList(issue));

		// Act
		issueController.listIssues(PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueRepository).findByProjectId(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssues(Arrays.asList(issue));
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProjectHasNoIssues_ShowsEmptyList() {
		// Arrange
		when(projectRepository.exists(PROJECT_ID)).thenReturn(true);
		when(issueRepository.findByProjectId(PROJECT_ID)).thenReturn(Collections.emptyList());

		// Act
		issueController.listIssues(PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueRepository).findByProjectId(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.listIssues(null);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.listIssues(EMPTY_STRING);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.listIssues(NON_NUMERIC_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		when(projectRepository.exists(PROJECT_ID)).thenReturn(false);

		// Act
		issueController.listIssues(PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedFieldsAreValid_CreatesNewIssue() {
		// Arrange
		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		when(projectRepository.exists(PROJECT_ID)).thenReturn(true);
		when(issueRepository.exists(ISSUE_ID)).thenReturn(false);
		when(issueRepository.findByProjectId(PROJECT_ID)).thenReturn(Arrays.asList(issue));

		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueRepository).exists(ISSUE_ID);
		inOrder.verify(issueRepository)
				.save(new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID));
		inOrder.verify(issueRepository).findByProjectId(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssues(
				Arrays.asList(new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID)));
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(null, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(EMPTY_STRING, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, null, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_NAME);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, EMPTY_STRING, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_NAME);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, null, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, EMPTY_STRING, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, null, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, EMPTY_STRING, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityDoesNotHaveExpectedValue_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, "NOT ALLOWED", PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NOT_ALLOWED_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, null);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, EMPTY_STRING);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, NON_NUMERIC_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.addIssue(NON_NUMERIC_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		when(projectRepository.exists(PROJECT_ID)).thenReturn(false);

		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdAlreadyExistInDatabase_ShowsErrorMessage() {
		// Arrange
		when(projectRepository.exists(PROJECT_ID)).thenReturn(true);
		when(issueRepository.exists(ISSUE_ID)).thenReturn(true);

		// Act
		issueController.addIssue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(PROJECT_ID);
		inOrder.verify(issueRepository).exists(ISSUE_ID);
		inOrder.verify(issueTrackerView).showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, ISSUE_ID));
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsValid_DeletesIssue() {
		// Arrange
		Issue issue = new Issue(ISSUE_ID, ISSUE_NAME, ISSUE_DESCRIPTION, ISSUE_PRIORITY, PROJECT_ID);
		when(issueRepository.exists(ISSUE_ID)).thenReturn(true);
		when(issueRepository.findById(ISSUE_ID)).thenReturn(issue);
		when(issueRepository.findByProjectId(PROJECT_ID)).thenReturn(Collections.emptyList());

		// Act
		issueController.deleteIssue(ISSUE_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(ISSUE_ID);
		inOrder.verify(issueRepository).findById(ISSUE_ID);
		inOrder.verify(issueRepository).delete(ISSUE_ID);
		inOrder.verify(issueRepository).findByProjectId(PROJECT_ID);
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
		verifyNoMoreInteractions(issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.deleteIssue(null);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.deleteIssue(EMPTY_STRING);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.deleteIssue(NON_NUMERIC_ID);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		when(issueRepository.exists(ISSUE_ID)).thenReturn(false);

		// Act
		issueController.deleteIssue(ISSUE_ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(ISSUE_ID);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.ISSUE_DOESNT_EXIST);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}
}
