package com.shemilikevin.app.tracker.repository.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.shemilikevin.app.tracker.model.Issue;
import com.shemilikevin.app.tracker.repository.IssueRepository;

public class IssueMongoRepository implements IssueRepository {

	private MongoCollection<Issue> issueCollection;

	public IssueMongoRepository(MongoClient mongoClient, String databaseName, String collectionName) {

		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		issueCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, Issue.class);
	}

	@Override
	public List<Issue> findAll() {
		return issueCollection.find().into(new ArrayList<Issue>());
	}

	@Override
	public Issue findById(String id) {
		return issueCollection.find(Filters.eq("id", id)).first();
	}

	@Override
	public List<Issue> findByProjectId(String projectId) {
		return issueCollection.find(Filters.eq("projectId", projectId)).into(new ArrayList<Issue>());
	}

	@Override
	public void save(Issue issue) {
		issueCollection.insertOne(issue);
	}

	@Override
	public void delete(String id) {
		issueCollection.deleteOne(Filters.eq("id", id));
	}

	@Override
	public boolean exists(String id) {

		Issue issue = issueCollection.find(Filters.eq("id", id)).first();

		return issue != null;
	}

	@Override
	public boolean hasAssociatedIssues(String projectId) {

		ArrayList<Issue> issueList = new ArrayList<>();
		issueCollection.find(Filters.eq("projectId", projectId)).into(issueList);

		return !issueList.isEmpty();
	}
}