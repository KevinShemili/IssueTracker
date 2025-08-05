package com.shemilikevin.app.tracker.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class ProjectControllerTest {

	private static final String NON_NUMERIC_ID = "XX";
	private static final String EMPTY_STRING = " ";
	private static final String ID = "1";
	private static final String NAME = "Desktop Application";
	private static final String DESCRIPTION = "Desktop Application Description";

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
		Project project = new Project(ID, NAME, DESCRIPTION);
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
		Project project = new Project(ID, NAME, DESCRIPTION);
		when(projectRepository.exists(ID)).thenReturn(false);
		when(projectRepository.findAll()).thenReturn(Arrays.asList(project));

		// Act
		projectController.addProject(ID, NAME, DESCRIPTION);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(ID);
		inOrder.verify(projectRepository).save(new Project(ID, NAME, DESCRIPTION));
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Arrays.asList(new Project(ID, NAME, DESCRIPTION)));
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(null, NAME, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_ID);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(EMPTY_STRING, NAME, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_ID);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdIsNonNumeric_ShowsErrorMessage() {
		// Act
		projectController.addProject(NON_NUMERIC_ID, NAME, DESCRIPTION);

		// Assert
		verify(issueTrackerView).showProjectError(Validators.NON_NUMERICAL_ID);
		verifyNoMoreInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectNameIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(ID, null, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_NAME);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectNameIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(ID, EMPTY_STRING, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_NAME);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectDescriptionIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(ID, NAME, null))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_DESCRIPTION);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectDescriptionIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(ID, NAME, EMPTY_STRING))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_DESCRIPTION);
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdAlreadyExistsInDatabase_ShowsDuplicationError() {
		// Arrange
		when(projectRepository.exists(ID)).thenReturn(true);

		// Act
		projectController.addProject(ID, NAME, DESCRIPTION);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(ID);
		inOrder.verify(issueTrackerView).showProjectError(String.format(Validators.DUPLICATE_PROJECT, ID));
		verifyNoMoreInteractions(issueTrackerView, projectRepository);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasNoAssociatedIssues_DeletesProject() {
		// Arrange
		when(projectRepository.exists(ID)).thenReturn(true);
		when(issueRepository.hasAssociatedIssues(ID)).thenReturn(false);
		when(projectRepository.findAll()).thenReturn(Collections.emptyList());

		// Act
		projectController.deleteProject(ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(ID);
		inOrder.verify(issueRepository).hasAssociatedIssues(ID);
		inOrder.verify(projectRepository).delete(ID);
		inOrder.verify(projectRepository).findAll();
		inOrder.verify(issueTrackerView).showProjects(Collections.emptyList());
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectHasAssociatedIssues_ShowsHasIssuesError() {
		// Arrange
		when(projectRepository.exists(ID)).thenReturn(true);
		when(issueRepository.hasAssociatedIssues(ID)).thenReturn(true);

		// Act
		projectController.deleteProject(ID);

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists(ID);
		inOrder.verify(issueRepository).hasAssociatedIssues(ID);
		inOrder.verify(issueTrackerView).showProjectError(Validators.PROJECT_HAS_ISSUES);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectIdDoesNotExistInDatabase_ThrowsIllegalArgumentException() {
		// Arrange
		when(projectRepository.exists(ID)).thenReturn(false);

		// Act & Assert
		assertThatThrownBy(() -> projectController.deleteProject(ID)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(Validators.PROJECT_DOESNT_EXIST);
		verify(projectRepository).exists(ID);
		verifyNoMoreInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectIdIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.deleteProject(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage(Validators.NULL_EMPTY_ID);
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.deleteProject(EMPTY_STRING))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NULL_EMPTY_ID);
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testDeleteProject_WhenProvidedProjectIdIsNonNumeric_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.deleteProject(NON_NUMERIC_ID))
				.isInstanceOf(IllegalArgumentException.class).hasMessage(Validators.NON_NUMERICAL_ID);
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}
}
