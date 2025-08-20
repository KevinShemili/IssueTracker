package com.shemilikevin.app.tracker.controller;

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

import com.shemilikevin.app.tracker.helpers.ErrorMessages;
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
		inOrder.verify(issueTrackerView).clearIssueFields();
		inOrder.verify(issueTrackerView).clearIssueSelection();
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
		inOrder.verify(issueTrackerView).clearIssueFields();
		inOrder.verify(issueTrackerView).clearIssueSelection();
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
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
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
		inOrder.verify(issueTrackerView).clearIssueFields();
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
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
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
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
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
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
		issueController.deleteIssue(issueId, projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueRepository).findById(issueId);
		inOrder.verify(issueRepository).delete(issueId);
		inOrder.verify(issueRepository).findByProjectId(projectId);
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
		inOrder.verify(issueTrackerView).clearIssueSelection();
		verifyNoMoreInteractions(issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteIssue_WhenProvidedIssueIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String issueId = "1";
		String projectId = "10";
		when(issueRepository.exists(issueId)).thenReturn(false);

		// Act
		issueController.deleteIssue(issueId, projectId);

		// Assert
		InOrder inOrder = Mockito.inOrder(issueRepository, issueTrackerView);
		inOrder.verify(issueRepository).exists(issueId);
		inOrder.verify(issueTrackerView).showIssueError(ErrorMessages.ISSUE_DOESNT_EXIST);
		inOrder.verify(issueRepository).findByProjectId(projectId);
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
		verifyNoMoreInteractions(issueRepository, issueTrackerView);
	}
}
