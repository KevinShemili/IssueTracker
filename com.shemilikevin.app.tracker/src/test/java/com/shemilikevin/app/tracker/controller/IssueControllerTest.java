package com.shemilikevin.app.tracker.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

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

	private static final String PRIORITY = "Low";

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
		Issue issue = new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1");
		when(projectRepository.exists("1")).thenReturn(true);
		when(issueRepository.findByProjectId("1")).thenReturn(Arrays.asList(issue));

		// Act
		issueController.listIssues("1");

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists("1");
		inOrder.verify(issueRepository).findByProjectId("1");
		inOrder.verify(issueTrackerView).showIssues(Arrays.asList(issue));
	}

	@Test
	public void testListIssues_WhenProjectHasNoIssues_ShowsEmptyList() {
		// Arrange
		when(projectRepository.exists("1")).thenReturn(true);
		when(issueRepository.findByProjectId("1")).thenReturn(Collections.emptyList());

		// Act
		issueController.listIssues("1");

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists("1");
		inOrder.verify(issueRepository).findByProjectId("1");
		inOrder.verify(issueTrackerView).showIssues(Collections.emptyList());
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> issueController.listIssues(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> issueController.listIssues(" ")).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdIsNonNumeric_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> issueController.listIssues("X")).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Project ID must be numerical.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testListIssues_WhenProvidedProjectIdDoesNotExistInDatabase_ThrowsIllegalArgumentException() {
		// Arrange
		when(projectRepository.exists("1")).thenReturn(false);

		// Act & Assert
		assertThatThrownBy(() -> issueController.listIssues("1")).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Project ID does not exist in the database.");
		verifyNoMoreInteractions(ignoreStubs(projectRepository, issueRepository, issueTrackerView));
	}

	@Test
	public void testAddIssue_WhenProvidedFieldsAreValid_CreatesNewIssue() {
		// Arrange
		when(projectRepository.exists("1")).thenReturn(true);
		when(issueRepository.exists("1")).thenReturn(false);
		when(issueRepository.findByProjectId("1")).thenReturn(
				Arrays.asList(new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1")));

		// Act
		issueController.addIssue("1", "Broken Button", "Button is not clickable when...", "Medium", "1");

		// Assert
		InOrder inOrder = Mockito.inOrder(projectRepository, issueRepository, issueTrackerView);
		inOrder.verify(projectRepository).exists("1");
		inOrder.verify(issueRepository).exists("1");
		inOrder.verify(issueRepository)
				.save(new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1"));
		inOrder.verify(issueRepository).findByProjectId("1");
		inOrder.verify(issueTrackerView).showIssues(
				Arrays.asList(new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1")));
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(null, randomString(), randomString(), PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(" ", randomString(), randomString(), PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), null, randomString(), PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue name must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueNameIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), " ", randomString(), PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue name must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), null, PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Issue description must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssueDescriptionIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), " ", PRIORITY, randomString()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Issue description must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), randomString(), null, randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue priority must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), randomString(), " ", randomString()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Issue priority must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedIssuePriorityDoesNotHaveExpectedValue_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(() -> issueController.addIssue(randomString(), randomString(), randomString(),
				"DOES NOT EXIST", randomString())).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Issue priority must be either Low, Medium or High.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsNull_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), randomString(), PRIORITY, null))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	@Test
	public void testAddIssue_WhenProvidedProjectIdIsEmpty_ThrowsIllegalArgumentException() {
		// Act & Assert
		assertThatThrownBy(
				() -> issueController.addIssue(randomString(), randomString(), randomString(), PRIORITY, " "))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Project ID must not be null or empty.");
		verifyNoInteractions(projectRepository, issueRepository, issueTrackerView);
	}

	private static String randomString() {
		return UUID.randomUUID().toString();
	}
}
