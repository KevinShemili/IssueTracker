package com.shemilikevin.app.tracker.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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

	private static final String ID_1 = "1";
	private static final String ID_2 = "2";
	private static final String ID_3 = "3";
	private static final String NAME_1 = "Broken Button";
	private static final String NAME_2 = "Performance Issue";
	private static final String NAME_3 = "3rd Party Integration Error";
	private static final String DESCRIPTION_1 = "Button is not clickable when...";
	private static final String DESCRIPTION_2 = "Retrieval of data is very slow in...";
	private static final String DESCRIPTION_3 = "Error while communicating with...";
	private static final String PRIORITY_LOW = "Low";
	private static final String PRIORITY_MEDIUM = "Medium";
	private static final String PRIORITY_HIGH = "High";
	private static final String PROJECT_ID_1 = "1";
	private static final String PROJECT_ID_2 = "2";

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
	public void testFindAll_EmptyDatabase_ReturnsEmptyList() {

		// Act
		List<Issue> issueList = issueRepository.findAll();

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindAll_ManyIssuesInTheDatabase_ReturnsAllIssues() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
		AddIssueToDb(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));

		// Act
		List<Issue> issueList = issueRepository.findAll();

		// Assert
		assertThat(issueList).hasSize(2);
		assertThat(issueList).containsExactly(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1),
				new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {

		// Act
		Issue issue = issueRepository.findById(ID_1);

		// Assert
		assertThat(issue).isNull();
	}

	@Test
	public void testFindById_OnlyOneIssueInTheDatabase_ReturnsTheIssue() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));

		// Act
		Issue issue = issueRepository.findById(ID_1);

		// Assert
		assertThat(issue).isNotNull();
		assertThat(issue).isEqualTo(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
	}

	@Test
	public void testFindById_ManyIssuesInTheDatabase_ReturnsTheIssue() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
		AddIssueToDb(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));

		// Act
		Issue issue = issueRepository.findById(ID_2);

		// Assert
		assertThat(issue).isNotNull();
		assertThat(issue).isEqualTo(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));
	}

	@Test
	public void testFindByProjectId_NoMatchingIdInDatabase_ReturnsEmptyList() {

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(PROJECT_ID_1);

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindByProjectId_IssuesExistButForDifferentProject_ReturnsEmptyList() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
		AddIssueToDb(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(PROJECT_ID_2);

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindByProjectId_OnlyOneMatchingIssue_ReturnsOneIssue() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(PROJECT_ID_1);

		// Assert
		assertThat(issueList).hasSize(1);
		assertThat(issueList).containsExactly(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
	}

	@Test
	public void testFindByProjectId_ManyMatchingIssues_ReturnsTheIssues() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
		AddIssueToDb(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(PROJECT_ID_1);

		// Assert
		assertThat(issueList).hasSize(2);
		assertThat(issueList).containsExactly(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1),
				new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_1));
	}

	@Test
	public void testFindByProjectId_ManyIssuesFromDifferentProjects_ReturnsOnlyMatchingIssues() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
		AddIssueToDb(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_2));
		AddIssueToDb(new Issue(ID_3, NAME_3, DESCRIPTION_3, PRIORITY_LOW, PROJECT_ID_2));

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(PROJECT_ID_2);

		// Assert
		assertThat(issueList).hasSize(2);
		assertThat(issueList).containsExactly(new Issue(ID_2, NAME_2, DESCRIPTION_2, PRIORITY_HIGH, PROJECT_ID_2),
				new Issue(ID_3, NAME_3, DESCRIPTION_3, PRIORITY_LOW, PROJECT_ID_2));
	}

	@Test
	public void testSave_SavesIssueInTheDatabase() {

		// Act
		issueRepository.save(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));

		// Assert
		assertThat(queryAllIssuesFromDb())
				.containsExactly(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));
	}

	@Test
	public void testDelete_DeletesIssueFromTheDatabase() {

		// Arrange
		AddIssueToDb(new Issue(ID_1, NAME_1, DESCRIPTION_1, PRIORITY_MEDIUM, PROJECT_ID_1));

		// Act
		issueRepository.delete(ID_1);

		// Assert
		assertThat(queryAllIssuesFromDb()).isEmpty();
	}

	private void AddIssueToDb(Issue issue) {
		issueCollection.insertOne(issue);
	}

	private List<Issue> queryAllIssuesFromDb() {
		return issueCollection.find().into(new ArrayList<Issue>());
	}
}
