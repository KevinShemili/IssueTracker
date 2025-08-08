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
		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority 1", "10");
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Priority 2", "20");
		AddIssueToDb(issue1);
		AddIssueToDb(issue2);

		// Act
		List<Issue> issueList = issueRepository.findAll();

		// Assert
		assertThat(issueList).hasSize(2)
				.containsExactly(issue1, issue2);
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {
		// Act
		Issue issue = issueRepository.findById("1");

		// Assert
		assertThat(issue).isNull();
	}

	@Test
	public void testFindById_OnlyOneIssueInTheDatabase_ReturnsTheIssue() {
		// Arrange
		String id = "1";

		Issue issue = new Issue(id, "Name", "Description", "Priority", "10");
		AddIssueToDb(issue);

		// Act
		Issue result = issueRepository.findById(id);

		// Assert
		assertThat(result).isNotNull()
				.isEqualTo(issue);
	}

	@Test
	public void testFindById_ManyIssuesInTheDatabase_ReturnsTheIssue() {
		// Arrange
		String targetId = "2";

		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority 1", "10");
		Issue issue2 = new Issue(targetId, "Name 2", "Description 2", "Priority 2", "20");
		AddIssueToDb(issue1);
		AddIssueToDb(issue2);

		// Act
		Issue issue = issueRepository.findById(targetId);

		// Assert
		assertThat(issue).isNotNull()
				.isEqualTo(issue2);
	}

	@Test
	public void testFindByProjectId_NoMatchingIdInDatabase_ReturnsEmptyList() {
		// Act
		List<Issue> issueList = issueRepository.findByProjectId("1");

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindByProjectId_IssuesExistButForDifferentProject_ReturnsEmptyList() {
		// Arrange
		AddIssueToDb(new Issue("1", "Name 1", "Description 1", "Priority 1", "10"));
		AddIssueToDb(new Issue("2", "Name 2", "Description 2", "Priority 2", "10"));

		// Act
		List<Issue> issueList = issueRepository.findByProjectId("999");

		// Assert
		assertThat(issueList).isEmpty();
	}

	@Test
	public void testFindByProjectId_OnlyOneMatchingIssue_ReturnsOneIssue() {
		// Arrange
		String projectId = "10";

		Issue issue = new Issue("1", "Name", "Description", "Priority", projectId);
		AddIssueToDb(issue);

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(projectId);

		// Assert
		assertThat(issueList).hasSize(1)
				.containsExactly(issue);
	}

	@Test
	public void testFindByProjectId_ManyMatchingIssues_ReturnsTheIssues() {
		// Arrange
		String projectId = "10";

		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority 1", projectId);
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Priority 2", projectId);
		AddIssueToDb(issue1);
		AddIssueToDb(issue2);

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(projectId);

		// Assert
		assertThat(issueList).hasSize(2)
				.containsExactly(issue1, issue2);
	}

	@Test
	public void testFindByProjectId_ManyIssuesFromDifferentProjects_ReturnsOnlyMatchingIssues() {
		// Arrange
		String projectId = "10";

		Issue issue1 = new Issue("1", "Name 1", "Description 1", "Priority 1", projectId);
		Issue issue2 = new Issue("2", "Name 2", "Description 2", "Priority 2", projectId);
		Issue issue3 = new Issue("3", "Name 3", "Description 3", "Priority 3", "20");
		AddIssueToDb(issue1);
		AddIssueToDb(issue2);
		AddIssueToDb(issue3);

		// Act
		List<Issue> issueList = issueRepository.findByProjectId(projectId);

		// Assert
		assertThat(issueList).hasSize(2)
				.containsExactly(issue1, issue2);
	}

	@Test
	public void testSave_SavesIssueInTheDatabase() {
		// Arrange
		Issue issue = new Issue("1", "Name", "Description", "Priority", "10");

		// Act
		issueRepository.save(issue);

		// Assert
		assertThat(queryAllIssuesFromDb()).containsExactly(issue);
	}

	@Test
	public void testDelete_DeletesIssueFromTheDatabase() {
		// Arrange
		String id = "1";

		Issue issue = new Issue(id, "Name", "Description", "Priority", "10");
		AddIssueToDb(issue);

		// Act
		issueRepository.delete(id);

		// Assert
		assertThat(queryAllIssuesFromDb()).isEmpty();
	}

	@Test
	public void testExists_EmptyDatabase_ReturnsFalse() {
		// Act
		boolean result = issueRepository.exists("1");

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void testExists_MatchingIdInDatabase_ReturnsTrue() {
		// Arrange
		String id = "1";

		Issue issue = new Issue(id, "Name", "Description", "Priority", "10");
		AddIssueToDb(issue);

		// Act
		boolean result = issueRepository.exists(id);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void testExists_NoMatchingIdInDatabase_ReturnsFalse() {
		// Arrange
		AddIssueToDb(new Issue("1", "Name", "Description", "Priority", "10"));

		// Act
		boolean result = issueRepository.exists("999");

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void testHasAssociatedIssues_GivenProjectHasNoAssociatedIssues_ReturnsFalse() {
		// Act
		boolean result = issueRepository.hasAssociatedIssues("1");

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void testHasAssociatedIssues_GivenProjectHasAssociatedIssues_ReturnsTrue() {
		// Arrange
		String projectId = "10";

		AddIssueToDb(new Issue("1", "Name", "Description", "Priority", projectId));

		// Act
		boolean result = issueRepository.hasAssociatedIssues(projectId);

		// Assert
		assertThat(result).isTrue();
	}

	private void AddIssueToDb(Issue issue) {
		issueCollection.insertOne(issue);
	}

	private List<Issue> queryAllIssuesFromDb() {
		return issueCollection.find().into(new ArrayList<Issue>());
	}
}
