package com.shemilikevin.app.tracker.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(EMPTY_STRING, NAME, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueTrackerView);
	}

	@Test
	public void testAddProject_WhenProvidedProjectIdIsNonNumeric_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> projectController.addProject(NON_NUMERIC_ID, NAME, DESCRIPTION))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Project ID must be numerical.");
		verifyNoInteractions(projectRepository, issueTrackerView);
	}
}
