package com.shemilikevin.app.tracker.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.view.IssueTrackerView;

public class IssueTrackerSwingView extends JFrame implements IssueTrackerView {

	private static final long serialVersionUID = 1L;
	private static final int TAB_PROJECTS = 0;
	private static final int TAB_ISSUES = 1;

	private JTabbedPane tabbedPane;
	private JPanel mainPane;
	private JTextField projectIdField;
	private JTextField projectNameField;
	private JTextField projectDescriptionField;
	private JButton addProjectButton;
	private JButton deleteProjectButton;
	private DefaultListModel<Project> projectListModel;
	private JList<Project> projectJList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					IssueTrackerSwingView frame = new IssueTrackerSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IssueTrackerSwingView() {
		setMinimumSize(new Dimension(450, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 725, 480);
		mainPane = new JPanel();
		mainPane.setPreferredSize(new Dimension(725, 480));
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPane);
		mainPane.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setName("tabbedPane");
		mainPane.add(tabbedPane);

		JPanel projectPanel = new JPanel();
		projectPanel.setName("projectPanel");
		tabbedPane.addTab("Projects", null, projectPanel, null);
		tabbedPane.setEnabledAt(TAB_PROJECTS, true);
		GridBagLayout gbl_projectPanel = new GridBagLayout();
		gbl_projectPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_projectPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_projectPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_projectPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		projectPanel.setLayout(gbl_projectPanel);

		KeyAdapter addButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addProjectButton.setEnabled(
						!projectIdField.getText().trim().isEmpty() && !projectNameField.getText().trim().isEmpty()
								&& !projectDescriptionField.getText().trim().isEmpty());
			}
		};

		JLabel projectIdLabel = new JLabel("ID");
		projectIdLabel.setName("projectIdLabel");
		GridBagConstraints gbc_projectIdLabel = new GridBagConstraints();
		gbc_projectIdLabel.insets = new Insets(0, 0, 5, 5);
		gbc_projectIdLabel.gridx = 0;
		gbc_projectIdLabel.gridy = 0;
		projectPanel.add(projectIdLabel, gbc_projectIdLabel);

		projectIdField = new JTextField();
		projectIdField.addKeyListener(addButtonEnabler);
		projectIdField.setName("projectIdField");
		GridBagConstraints gbc_projectIdField = new GridBagConstraints();
		gbc_projectIdField.insets = new Insets(0, 0, 5, 0);
		gbc_projectIdField.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectIdField.gridx = 1;
		gbc_projectIdField.gridy = 0;
		projectPanel.add(projectIdField, gbc_projectIdField);
		projectIdField.setColumns(10);

		JLabel projectNameLabel = new JLabel("NAME");
		projectNameLabel.setName("projectNameLabel");
		GridBagConstraints gbc_projectNameLabel = new GridBagConstraints();
		gbc_projectNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_projectNameLabel.gridx = 0;
		gbc_projectNameLabel.gridy = 1;
		projectPanel.add(projectNameLabel, gbc_projectNameLabel);

		projectNameField = new JTextField();
		projectNameField.addKeyListener(addButtonEnabler);
		projectNameField.setName("projectNameField");
		GridBagConstraints gbc_projectNameField = new GridBagConstraints();
		gbc_projectNameField.insets = new Insets(0, 0, 5, 0);
		gbc_projectNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectNameField.gridx = 1;
		gbc_projectNameField.gridy = 1;
		projectPanel.add(projectNameField, gbc_projectNameField);
		projectNameField.setColumns(10);

		JLabel projectDescriptionLabel = new JLabel("DESCRIPTION");
		projectDescriptionLabel.setName("projectDescriptionLabel");
		GridBagConstraints gbc_projectDescriptionLabel = new GridBagConstraints();
		gbc_projectDescriptionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_projectDescriptionLabel.gridx = 0;
		gbc_projectDescriptionLabel.gridy = 2;
		projectPanel.add(projectDescriptionLabel, gbc_projectDescriptionLabel);

		projectDescriptionField = new JTextField();
		projectDescriptionField.addKeyListener(addButtonEnabler);
		projectDescriptionField.setName("projectDescriptionField");
		GridBagConstraints gbc_projectDescriptionField = new GridBagConstraints();
		gbc_projectDescriptionField.insets = new Insets(0, 0, 5, 0);
		gbc_projectDescriptionField.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectDescriptionField.gridx = 1;
		gbc_projectDescriptionField.gridy = 2;
		projectPanel.add(projectDescriptionField, gbc_projectDescriptionField);
		projectDescriptionField.setColumns(10);

		JLabel errorLabel = new JLabel(" ");
		errorLabel.setForeground(new Color(255, 0, 0));
		errorLabel.setName("errorLabel");
		GridBagConstraints gbc_errorLabel = new GridBagConstraints();
		gbc_errorLabel.insets = new Insets(0, 0, 5, 0);
		gbc_errorLabel.gridwidth = 2;
		gbc_errorLabel.gridx = 0;
		gbc_errorLabel.gridy = 3;
		projectPanel.add(errorLabel, gbc_errorLabel);

		JScrollPane projectScrollPane = new JScrollPane();
		projectScrollPane.setName("projectScrollPane");
		GridBagConstraints gbc_projectScrollPane = new GridBagConstraints();
		gbc_projectScrollPane.gridheight = 3;
		gbc_projectScrollPane.gridwidth = 2;
		gbc_projectScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_projectScrollPane.fill = GridBagConstraints.BOTH;
		gbc_projectScrollPane.gridx = 0;
		gbc_projectScrollPane.gridy = 4;
		projectPanel.add(projectScrollPane, gbc_projectScrollPane);

		projectListModel = new DefaultListModel<Project>();
		projectJList = new JList<Project>(projectListModel);
		projectJList.addListSelectionListener(e -> {
			boolean isSelectionEmpty = projectJList.isSelectionEmpty();
			tabbedPane.setEnabledAt(TAB_ISSUES, !isSelectionEmpty);
			deleteProjectButton.setEnabled(!isSelectionEmpty);
		});
		projectJList.setName("projectList");
		projectScrollPane.setViewportView(projectJList);

		JPanel projectButtonsPanel = new JPanel();
		projectButtonsPanel.setName("projectButtonsPanel");
		GridBagConstraints gbc_projectButtonsPanel = new GridBagConstraints();
		gbc_projectButtonsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectButtonsPanel.gridwidth = 2;
		gbc_projectButtonsPanel.gridx = 0;
		gbc_projectButtonsPanel.gridy = 7;
		projectPanel.add(projectButtonsPanel, gbc_projectButtonsPanel);

		addProjectButton = new JButton("ADD");
		addProjectButton.setEnabled(false);
		addProjectButton.setName("addProjectButton");
		projectButtonsPanel.add(addProjectButton);

		deleteProjectButton = new JButton("DELETE");
		deleteProjectButton.setEnabled(false);
		deleteProjectButton.setName("deleteProjectButton");
		projectButtonsPanel.add(deleteProjectButton);

		JPanel issuePanel = new JPanel();
		issuePanel.setName("issuePanel");
		tabbedPane.addTab("Issues", null, issuePanel, null);
		tabbedPane.setEnabledAt(TAB_ISSUES, false);
		GridBagLayout gbl_issuePanel = new GridBagLayout();
		gbl_issuePanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_issuePanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_issuePanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_issuePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		issuePanel.setLayout(gbl_issuePanel);

	}

	@Override
	public void showIssues(List<Issue> issueList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showProjects(List<Project> projectList) {
		// TODO Auto-generated method stub

	}

	public DefaultListModel<Project> getProjectListModel() {
		return projectListModel;
	}
}
