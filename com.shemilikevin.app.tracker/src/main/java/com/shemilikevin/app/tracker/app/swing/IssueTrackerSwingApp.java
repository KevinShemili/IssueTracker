package com.shemilikevin.app.tracker.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	@Option(names = { "--mongo-host" }, description = "MongoDB Host Address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB Host Port")
	private int mongoPort = 27017;

	private final String databaseName = "database";
	private final String projectCollectionName = "project";
	private final String collectionName = "issue";

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
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});

		return null;
	}
}