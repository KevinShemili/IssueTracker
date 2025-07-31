package com.shemilikevin.app.tracker.repository.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.shemilikevin.app.tracker.model.Issue;

public class IssueMongoRepository {

	private MongoCollection<Issue> issueCollection;

	public IssueMongoRepository(MongoClient mongoClient, String databaseName, String collectionName) {

		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		issueCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, Issue.class);
	}

	public List<Issue> findAll() {

		return issueCollection.find().into(new ArrayList<Issue>());
	}

	public Issue findById(String string) {
		return null;
	}

}
