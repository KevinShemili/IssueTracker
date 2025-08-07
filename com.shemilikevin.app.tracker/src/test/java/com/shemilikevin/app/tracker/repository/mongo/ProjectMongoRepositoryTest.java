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
import com.shemilikevin.app.tracker.model.Project;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ProjectMongoRepositoryTest {

	private static final String DATABASE_NAME = "db";
	private static final String COLLECTION_NAME = "collection";

	private static MongoServer mongoServer;
	private static InetSocketAddress inetSocketAddress;

	private MongoClient mongoClient;
	private ProjectMongoRepository projectRepository;
	private MongoCollection<Project> projectCollection;

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

		projectRepository = new ProjectMongoRepository(mongoClient, DATABASE_NAME, COLLECTION_NAME);

		MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
		database.drop();

		projectCollection = database.getCollection(COLLECTION_NAME, Project.class);
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Test
	public void testFindAll_EmptyDatabase_ReturnsEmptyList() {
		// Act
		List<Project> projectList = projectRepository.findAll();

		// Assert
		assertThat(projectList).isEmpty();
	}

	@Test
	public void testFindAll_ManyProjectsInTheDatabase_ReturnsAllProjects() {
		// Arrange
		Project project1 = new Project("1", "Name 1", "Description 1");
		Project project2 = new Project("2", "Name 2", "Description 2");
		addProjectToDb(project1);
		addProjectToDb(project2);

		// Act
		List<Project> projectList = projectRepository.findAll();

		// Assert
		assertThat(projectList).hasSize(2);
		assertThat(projectList).containsExactly(project1, project2);
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {
		// Act
		Project project = projectRepository.findById("999");

		// Assert
		assertThat(project).isNull();
	}

	@Test
	public void testFindById_OnlyOneProjectInTheDatabase_ReturnsTheProject() {
		// Arrange
		String id = "1";

		Project project = new Project(id, "Name", "Description");
		addProjectToDb(project);

		// Act
		Project result = projectRepository.findById(id);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(project);
	}

	@Test
	public void testFindById_ManyProjectsInTheDatabase_ReturnsTheProject() {
		// Arrange
		String targetId = "1";

		Project project1 = new Project(targetId, "Name 1", "Description 1");
		Project project2 = new Project("2", "Name 2", "Description 2");
		addProjectToDb(project1);
		addProjectToDb(project2);

		// Act
		Project project = projectRepository.findById(targetId);

		// Assert
		assertThat(project).isNotNull();
		assertThat(project).isEqualTo(project1);
	}

	@Test
	public void testSave_SavesProjectInTheDatabase() {
		// Arrange
		Project project = new Project("1", "Name", "Description");

		// Act
		projectRepository.save(project);

		// Assert
		assertThat(queryAllProjectsFromDb()).containsExactly(project);
	}

	@Test
	public void testDelete_DeletesProjectFromTheDatabase() {
		// Arrange
		String id = "1";
		addProjectToDb(new Project(id, "Name", "Description"));

		// Act
		projectRepository.delete(id);

		// Assert
		assertThat(queryAllProjectsFromDb()).isEmpty();
	}

	@Test
	public void testExists_EmptyDatabase_ReturnsFalse() {
		// Act
		boolean result = projectRepository.exists("999");

		// Assert
		assertThat(result).isFalse();
	}

	@Test
	public void testExists_MatchingIdInDatabase_ReturnsTrue() {
		// Arrange
		String id = "1";
		addProjectToDb(new Project(id, "Name", "Description"));

		// Act
		boolean result = projectRepository.exists(id);

		// Assert
		assertThat(result).isTrue();
	}

	@Test
	public void testExists_NoMatchingIdInDatabase_ReturnsFalse() {
		// Arrange
		addProjectToDb(new Project("1", "Name", "Description"));

		// Act
		boolean result = projectRepository.exists("999");

		// Assert
		assertThat(result).isFalse();
	}

	private void addProjectToDb(Project project) {
		projectCollection.insertOne(project);
	}

	private List<Project> queryAllProjectsFromDb() {
		return projectCollection.find().into(new ArrayList<Project>());
	}
}
