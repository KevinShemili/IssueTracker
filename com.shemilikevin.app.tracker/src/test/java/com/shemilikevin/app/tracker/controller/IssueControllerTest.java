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
		String projectId = "10";
		Issue issue = new Issue("1", "Name", "Description", "Priority", projectId);

		when(projectRepository.exists(projectId)).thenReturn(true);
		when(issueRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(issue));

		// Act
		issueController.listIssues(projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueRepository).findByProjectId(projectId);
		inOrder.verify(issueTrackerView).showIssues(Arrays.asList(issue));
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProjectHasNoIssues_ShowsEmptyList() {
		// Arrange
		String projectId = "10";

		when(projectRepository.exists(projectId)).thenReturn(true);
		when(issueRepository.findByProjectId(projectId)).thenReturn(Collections.emptyList());

		// Act
		issueController.listIssues(projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueRepository).findByProjectId(projectId);
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
		issueController.listIssues(" ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.listIssues("XYZ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String projectId = "10";

		when(projectRepository.exists(projectId)).thenReturn(false);

		// Act
		issueController.listIssues(projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedFieldsAreValid_CreatesNewIssue() {
		// Arrange
		String issueId = "1";
		String name = "Name";
		String description = "Description";
		String priority = "Low";
		String projectId = "10";

		Issue issue = new Issue(issueId, name, description, priority, projectId);

		when(projectRepository.exists(projectId)).thenReturn(true);
		when(issueRepository.exists(issueId)).thenReturn(false);
		when(issueRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(issue));

		// Act
		issueController.addIssue(issueId, name, description, priority, projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueRepository).save(issue);
		inOrder.verify(issueRepository).findByProjectId(projectId);
		inOrder.verify(issueTrackerView).showIssues(Arrays.asList(issue));
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue(null, "Name", "Description", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue(" ", "Name", "Description", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", null, "Description", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_NAME);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", " ", "Description", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_NAME);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", null, "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", " ", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_DESCRIPTION);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", null, "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", " ", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityDoesNotHaveExpectedValue_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", "Random", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NOT_ALLOWED_PRIORITY);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsNull_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", "Low", null);

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsEmpty_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", "Low", " ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.addIssue("1", "Name", "Description", "Low", "XYZ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.addIssue("XYZ", "Name", "Description", "Low", "10");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String projectId = "10";

		when(projectRepository.exists(projectId)).thenReturn(false);

		// Act
		issueController.addIssue("1", "Name", "Description", "Low", projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.PROJECT_DOESNT_EXIST);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdAlreadyExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String projectId = "10";
		String issueId = "1";

		when(projectRepository.exists(projectId)).thenReturn(true);
		when(issueRepository.exists(issueId)).thenReturn(true);

		// Act
		issueController.addIssue(issueId, "Name", "Description", "Low", projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(projectId);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueTrackerView).showIssueError(String.format(ErrorMessages.DUPLICATE_ISSUE, issueId));
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsValid_DeletesIssue() {
		// Arrange
		String projectId = "10";
		String issueId = "1";
		Issue issue = new Issue(issueId, "Name", "Description", "Low", projectId);

		when(issueRepository.exists(issueId)).thenReturn(true);
		when(issueRepository.findById(issueId)).thenReturn(issue);
		when(issueRepository.findByProjectId(projectId)).thenReturn(Collections.emptyList());

		// Act
		issueController.deleteIssue(issueId);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueRepository).findById(issueId);
		inOrder.verify(issueRepository).delete(issueId);
		inOrder.verify(issueRepository).findByProjectId(projectId);
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
		issueController.deleteIssue(" ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NULL_EMPTY_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		issueController.deleteIssue("XYZ");

		// Assert
		verify(issueTrackerView).showIssueError(ErrorMessages.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String issueId = "1";
		when(issueRepository.exists(issueId)).thenReturn(false);

		// Act
		issueController.deleteIssue(issueId);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.ISSUE_DOESNT_EXIST);
		verifyNoMoreInteractions(issueTrackerView, projectRepository, issueRepository);
	}
}
