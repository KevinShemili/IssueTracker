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
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class ProjectControllerTest {

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private IssueRepository issueRepository;

	@Mock
	private IssueTrackerView issueTrackerView;

	@InjectMocks
	private ProjectController projectController;

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
	public void testListProjects_WhenThereAreProjectsInTheDatabase_ShowsAllProjects() {
		// Arrange
		Project project = new Project("1", "Name", "Description");

		when(projectRepository.findAll()).thenReturn(Arrays.asList(project));

		// Act
		projectController.listProjects();

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Arrays.asList(project));
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testListProjects_WhenThereNoProjectsInTheDatabase_ShowsEmptyList() {
		// Arrange
		when(projectRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		projectController.listProjects();

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Collections.emptyList());
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedFieldsAreValid_CreatesNewProject() {
		// Arrange
		String id = "1";
		String name = "Name";
		String description = "Description";
		Project project = new Project(id, name, description);

		when(projectRepository.exists(id)).thenReturn(false);
		when(projectRepository.findAll()).thenReturn(Arrays.asList(project));

		// Act
		projectController.addProject(id, name, description);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(id);
		inOrder.verify(projectRepository).save(project);
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Arrays.asList(project));
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdAlreadyExistsInDatabase_ShowsErrorMessage() {
		// Arrange
		String id = "1";
		when(projectRepository.exists(id)).thenReturn(true);

		// Act
		projectController.addProject(id, "Name", "Description");

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(id);
		inOrder.verify(issueTrackerView).showProjectError(String.format(ErrorMessages.DUPLICATE_PROJECT, id));
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasNoAssociatedIssues_DeletesProject() {
		// Arrange
		String id = "1";
		when(projectRepository.exists(id)).thenReturn(true);
		when(issueRepository.hasAssociatedIssues(id)).thenReturn(false);
		when(projectRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		projectController.deleteProject(id);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(id);
		inOrder.verify(issueRepository).hasAssociatedIssues(id);
		inOrder.verify(projectRepository).delete(id);
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Collections.emptyList());
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasAssociatedIssues_ShowsErrorMessage() {
		// Arrange
		String id = "1";

		when(projectRepository.exists(id)).thenReturn(true);
		when(issueRepository.hasAssociatedIssues(id)).thenReturn(true);

		// Act
		projectController.deleteProject(id);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(id);
		inOrder.verify(issueRepository).hasAssociatedIssues(id);
		inOrder.verify(issueTrackerView).showProjectError(ErrorMessages.PROJECT_HAS_ISSUES);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectIdDoesNotExistInDatabase_ShowsErrorMessage() {
		// Arrange
		String id = "1";

		when(projectRepository.exists(id)).thenReturn(false);

		// Act
		projectController.deleteProject(id);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(id);
		inOrder.verify(issueTrackerView).showProjectError(ErrorMessages.PROJECT_DOESNT_EXIST);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}
}
