package com.shemilikevin.app.tracker.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.shemilikevin.app.tracker.controller.IssueController;
import com.shemilikevin.app.tracker.controller.ProjectController;
import com.shemilikevin.app.tracker.repository.IssueRepository;
import com.shemilikevin.app.tracker.repository.ProjectRepository;
import com.shemilikevin.app.tracker.repository.mongo.IssueMongoRepository;
import com.shemilikevin.app.tracker.repository.mongo.ProjectMongoRepository;
import com.shemilikevin.app.tracker.view.swing.IssueTrackerSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class IssueTrackerSwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "database";

	@Option(names = { "--db-project-collection" }, description = "Project Collection Name")
	private String projectCollectionName = "project";

	@Option(names = { "--db-issue-collection" }, description = "Issue Collection Name")
	private String collectionName = "issue";

	public static void main(String[] args) {
		new CommandLine(new IssueTrackerSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				ServerAddress address = new ServerAddress(mongoHost, mongoPort);
				MongoClient mongoClient = new MongoClient(address);

				ProjectRepository projectRepository = new ProjectMongoRepository(mongoClient, databaseName,
						projectCollectionName);
				IssueRepository issueRepository = new IssueMongoRepository(mongoClient, databaseName, collectionName);

				IssueTrackerSwingView issueTrackerView = new IssueTrackerSwingView();

				ProjectController projectController = new ProjectController(projectRepository, issueRepository,
						issueTrackerView);
				IssueController issueController = new IssueController(projectRepository, issueRepository,
						issueTrackerView);

				issueTrackerView.setProjectController(projectController);
				issueTrackerView.setIssueController(issueController);

				issueTrackerView.setVisible(true);
				projectController.listProjects();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return null;
	}
}