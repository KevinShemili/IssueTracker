package com.shemilikevin.app.tracker.repository.mongo;

import java.net.InetSocketAddress;

import com.mongodb.MongoClient;

import de.bwaldvogel.mongo.MongoServer;

public class IssueMongoRepositoryTest {

	private static MongoServer mongoServer;
	private static InetSocketAddress inetSocketAddress;

	private MongoClient mongoClient;
	private IssueMongoRepository issueRepository;
}
