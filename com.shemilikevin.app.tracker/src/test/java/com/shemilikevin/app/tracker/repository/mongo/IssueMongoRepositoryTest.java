package com.shemilikevin.app.tracker.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.shemilikevin.app.tracker.model.Issue;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class IssueMongoRepositoryTest {

	private static final String DATABASE_NAME = "db";
	private static final String COLLECTION_NAME = "collection";
	private static MongoServer mongoServer;
	private static InetSocketAddress inetSocketAddress;

	private MongoClient mongoClient;
	private IssueMongoRepository issueRepository;
	private MongoCollection<Issue> issueCollection;

	@BeforeClass
	public static void setUpInMemoryServer() {
		mongoServer = new MongoServer(new MemoryBackend());
		inetSocketAddress = mongoServer.bind();
	}

	@AfterClass
	public static void shutDownInMemoryServer() {
		mongoServer.shutdown();
	}

	@Before
	public void setUp() {
		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		MongoClientOptions mongoClientOptions = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

		mongoClient = new MongoClient(new ServerAddress(inetSocketAddress), mongoClientOptions);
		issueRepository = new IssueMongoRepository(mongoClient, DATABASE_NAME, COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		database.drop();
		issueCollection = database.getCollection(COLLECTION_NAME, Issue.class);
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Test
	public void testFindAll_EmptyDatabase_ReturnsEmpty() {

		// Act
		List<Issue> issueList = issueRepository.findAll();

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindAll_DatabaseHasEntries_ReturnsEntries() {

		// Arrange
		Issue issue1 = new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1");
		Issue issue2 = new Issue("2", "Performance Issue", "Retrieval of data is very slow in...", "High", "1");
		issueCollection.insertMany(Arrays.asList(issue1, issue2));

		// Act
		List<Issue> issueList = issueRepository.findAll();

		// Assert
		assertThat(issueList).hasSize(2);
		assertThat(issueList).containsExactly(
				new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1"),
				new Issue("2", "Performance Issue", "Retrieval of data is very slow in...", "High", "1"));
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {

		// Act
		Issue issue = issueRepository.findById("1");

		// Assert
		assertThat(issue).isNull();
	}

	@Test
	public void testFindById_OnlyOneDatabaseEntry_EntryHasMatchingId_ReturnsEntry() {

		// Arrange
		issueCollection.insertOne(new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1"));

		// Act
		Issue issue = issueRepository.findById("1");

		// Assert
		assertThat(issue).isNotNull();
		assertThat(issue).isEqualTo(new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1"));
	}

	@Test
	public void testFindById_ManyDatabaseEntries_OneOfThemHasMatchingId_ReturnsEntry() {

		// Arrange
		Issue issue1 = new Issue("1", "Broken Button", "Button is not clickable when...", "Medium", "1");
		Issue issue2 = new Issue("2", "Performance Issue", "Retrieval of data is very slow in...", "High", "1");
		issueCollection.insertMany(Arrays.asList(issue1, issue2));

		// Act
		Issue issue = issueRepository.findById("2");

		// Assert
		assertThat(issue).isNotNull();
		assertThat(issue)
				.isEqualTo(new Issue("2", "Performance Issue", "Retrieval of data is very slow in...", "High", "1"));
	}
}
