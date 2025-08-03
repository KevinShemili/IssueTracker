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
import javax.swing.JComboBox;
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
	private JLabel issueIdLabel;
	private JTextField issueIdField;
	private JLabel issueNameLabel;
	private JTextField issueNameField;
	private JLabel issueDescriptionLabel;
	private JTextField issueDescriptionField;
	private JLabel issuePriorityLabel;
	private JComboBox<String> issuePriorityComboBox;
	private JLabel issueErrorLabel;
	private JScrollPane issueScrollPane;
	private JPanel issueButtonsPanel;
	private JButton addIssueButton;
	private JButton deleteIssueButton;
	private DefaultListModel<Issue> issueListModel;
	private JList<Issue> issueJList;

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

		KeyAdapter projectAddButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addProjectButton.setEnabled(
						!projectIdField.getText().trim().isEmpty() && !projectNameField.getText().trim().isEmpty()
								&& !projectDescriptionField.getText().trim().isEmpty());
			}
		};

		KeyAdapter issueAddButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				addIssueButton.setEnabled(
						!issueIdField.getText().trim().isEmpty() && !issueNameField.getText().trim().isEmpty()
								&& !issueDescriptionField.getText().trim().isEmpty()
								&& issuePriorityComboBox.getSelectedIndex() != -1);
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
		projectIdField.addKeyListener(projectAddButtonEnabler);
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
		projectNameField.addKeyListener(projectAddButtonEnabler);
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
		projectDescriptionField.addKeyListener(projectAddButtonEnabler);
		projectDescriptionField.setName("projectDescriptionField");
		GridBagConstraints gbc_projectDescriptionField = new GridBagConstraints();
		gbc_projectDescriptionField.insets = new Insets(0, 0, 5, 0);
		gbc_projectDescriptionField.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectDescriptionField.gridx = 1;
		gbc_projectDescriptionField.gridy = 2;
		projectPanel.add(projectDescriptionField, gbc_projectDescriptionField);
		projectDescriptionField.setColumns(10);

		JLabel projectErrorLabel = new JLabel(" ");
		projectErrorLabel.setForeground(new Color(255, 0, 0));
		projectErrorLabel.setName("projectErrorLabel");
		GridBagConstraints gbc_projectErrorLabel = new GridBagConstraints();
		gbc_projectErrorLabel.insets = new Insets(0, 0, 5, 0);
		gbc_projectErrorLabel.gridwidth = 2;
		gbc_projectErrorLabel.gridx = 0;
		gbc_projectErrorLabel.gridy = 3;
		projectPanel.add(projectErrorLabel, gbc_projectErrorLabel);

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

		issueIdLabel = new JLabel("ID");
		issueIdLabel.setName("issueIdLabel");
		GridBagConstraints gbc_issueIdLabel = new GridBagConstraints();
		gbc_issueIdLabel.insets = new Insets(0, 0, 5, 5);
		gbc_issueIdLabel.gridx = 0;
		gbc_issueIdLabel.gridy = 0;
		issuePanel.add(issueIdLabel, gbc_issueIdLabel);

		issueIdField = new JTextField();
		issueIdField.setName("issueIdField");
		issueIdField.addKeyListener(issueAddButtonEnabler);
		GridBagConstraints gbc_issueIdField = new GridBagConstraints();
		gbc_issueIdField.insets = new Insets(0, 0, 5, 0);
		gbc_issueIdField.fill = GridBagConstraints.HORIZONTAL;
		gbc_issueIdField.gridx = 1;
		gbc_issueIdField.gridy = 0;
		issuePanel.add(issueIdField, gbc_issueIdField);
		issueIdField.setColumns(10);

		issueNameLabel = new JLabel("NAME");
		issueNameLabel.setName("issueNameLabel");
		GridBagConstraints gbc_issueNameLabel = new GridBagConstraints();
		gbc_issueNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_issueNameLabel.gridx = 0;
		gbc_issueNameLabel.gridy = 1;
		issuePanel.add(issueNameLabel, gbc_issueNameLabel);

		issueNameField = new JTextField();
		issueNameField.setName("issueNameField");
		issueNameField.addKeyListener(issueAddButtonEnabler);
		GridBagConstraints gbc_issueNameField = new GridBagConstraints();
		gbc_issueNameField.insets = new Insets(0, 0, 5, 0);
		gbc_issueNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_issueNameField.gridx = 1;
		gbc_issueNameField.gridy = 1;
		issuePanel.add(issueNameField, gbc_issueNameField);
		issueNameField.setColumns(10);

		issueDescriptionLabel = new JLabel("DESCRIPTION");
		issueDescriptionLabel.setName("issueDescriptionLabel");
		GridBagConstraints gbc_issueDescriptionLabel = new GridBagConstraints();
		gbc_issueDescriptionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_issueDescriptionLabel.gridx = 0;
		gbc_issueDescriptionLabel.gridy = 2;
		issuePanel.add(issueDescriptionLabel, gbc_issueDescriptionLabel);

		issueDescriptionField = new JTextField();
		issueDescriptionField.setName("issueDescriptionField");
		issueDescriptionField.addKeyListener(issueAddButtonEnabler);
		GridBagConstraints gbc_issueDescriptionField = new GridBagConstraints();
		gbc_issueDescriptionField.insets = new Insets(0, 0, 5, 0);
		gbc_issueDescriptionField.fill = GridBagConstraints.HORIZONTAL;
		gbc_issueDescriptionField.gridx = 1;
		gbc_issueDescriptionField.gridy = 2;
		issuePanel.add(issueDescriptionField, gbc_issueDescriptionField);
		issueDescriptionField.setColumns(10);

		issuePriorityLabel = new JLabel("PRIORITY");
		issuePriorityLabel.setName("issuePriorityLabel");
		GridBagConstraints gbc_issuePriorityLabel = new GridBagConstraints();
		gbc_issuePriorityLabel.insets = new Insets(0, 0, 5, 5);
		gbc_issuePriorityLabel.gridx = 0;
		gbc_issuePriorityLabel.gridy = 3;
		issuePanel.add(issuePriorityLabel, gbc_issuePriorityLabel);

		issuePriorityComboBox = new JComboBox<String>();
		issuePriorityComboBox.setName("issuePriorityComboBox");
		issuePriorityComboBox.addItem("Low");
		issuePriorityComboBox.addItem("Medium");
		issuePriorityComboBox.addItem("High");
		issuePriorityComboBox.setSelectedItem(null);
		issuePriorityComboBox.addActionListener(e -> {
			addIssueButton.setEnabled(!issueIdField.getText().trim().isEmpty()
					&& !issueNameField.getText().trim().isEmpty() && !issueDescriptionField.getText().trim().isEmpty()
					&& issuePriorityComboBox.getSelectedIndex() != -1);
		});
		GridBagConstraints gbc_issuePriorityComboBox = new GridBagConstraints();
		gbc_issuePriorityComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_issuePriorityComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_issuePriorityComboBox.gridx = 1;
		gbc_issuePriorityComboBox.gridy = 3;
		issuePanel.add(issuePriorityComboBox, gbc_issuePriorityComboBox);

		issueErrorLabel = new JLabel(" ");
		issueErrorLabel.setForeground(new Color(255, 0, 0));
		issueErrorLabel.setName("issueErrorLabel");
		GridBagConstraints gbc_issueErrorLabel = new GridBagConstraints();
		gbc_issueErrorLabel.gridwidth = 2;
		gbc_issueErrorLabel.insets = new Insets(0, 0, 5, 0);
		gbc_issueErrorLabel.gridx = 0;
		gbc_issueErrorLabel.gridy = 4;
		issuePanel.add(issueErrorLabel, gbc_issueErrorLabel);

		issueScrollPane = new JScrollPane();
		issueScrollPane.setName("issueScrollPane");
		GridBagConstraints gbc_issueScrollPane = new GridBagConstraints();
		gbc_issueScrollPane.gridheight = 10;
		gbc_issueScrollPane.gridwidth = 2;
		gbc_issueScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_issueScrollPane.fill = GridBagConstraints.BOTH;
		gbc_issueScrollPane.gridx = 0;
		gbc_issueScrollPane.gridy = 5;
		issuePanel.add(issueScrollPane, gbc_issueScrollPane);

		issueListModel = new DefaultListModel<Issue>();
		issueJList = new JList<Issue>(issueListModel);
		issueJList.addListSelectionListener(e -> {
			boolean isSelectionEmpty = issueJList.isSelectionEmpty();
			deleteIssueButton.setEnabled(!isSelectionEmpty);
		});
		issueJList.setName("issueList");
		issueScrollPane.setViewportView(issueJList);

		issueButtonsPanel = new JPanel();
		issueButtonsPanel.setName("issueButtonsPanel");
		GridBagConstraints gbc_issueButtonsPanel = new GridBagConstraints();
		gbc_issueButtonsPanel.gridwidth = 2;
		gbc_issueButtonsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_issueButtonsPanel.gridx = 0;
		gbc_issueButtonsPanel.gridy = 15;
		issuePanel.add(issueButtonsPanel, gbc_issueButtonsPanel);

		addIssueButton = new JButton("ADD");
		addIssueButton.setEnabled(false);
		addIssueButton.setName("addIssueButton");
		issueButtonsPanel.add(addIssueButton);

		deleteIssueButton = new JButton("DELETE");
		deleteIssueButton.setEnabled(false);
		deleteIssueButton.setName("deleteIssueButton");
		issueButtonsPanel.add(deleteIssueButton);

		tabbedPane.addChangeListener(e -> {
			if (tabbedPane.getSelectedIndex() == TAB_PROJECTS) {
				clearAllUserInput();
				tabbedPane.setEnabledAt(TAB_ISSUES, false);
			}
		});

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
		projectListModel.clear();

		for (Project project : projectList) {
			projectListModel.addElement(project);
		}

	}

	public DefaultListModel<Project> getProjectListModel() {
		return projectListModel;
	}

	public DefaultListModel<Issue> getIssueListModel() {
		return issueListModel;
	}

	private void clearAllUserInput() {
		projectIdField.setText("");
		issueIdField.setText("");
		projectNameField.setText("");
		issueNameField.setText("");
		projectDescriptionField.setText("");
		issueDescriptionField.setText("");
		issuePriorityComboBox.setSelectedItem(null);
		issueListModel.clear();
		projectJList.clearSelection();

		addProjectButton.setEnabled(false);
		deleteProjectButton.setEnabled(false);
		addIssueButton.setEnabled(false);
		deleteIssueButton.setEnabled(false);
	}
}
