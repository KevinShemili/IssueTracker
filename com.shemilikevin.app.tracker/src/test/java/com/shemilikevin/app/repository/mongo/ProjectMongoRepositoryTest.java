package com.shemilikevin.app.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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
import com.shemilikevin.app.model.Project;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ProjectMongoRepositoryTest {
	private static MongoServer mongoServer;
	private static InetSocketAddress inetSocketAddress;

	private MongoClient mongoClient;
	private ProjectMongoRepository projectRepository;
	private MongoCollection<Project> projectCollection;
	private String databaseName = "db";
	private String collectionName = "collection";

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
		projectRepository = new ProjectMongoRepository(mongoClient, databaseName, collectionName);
		MongoDatabase database = mongoClient.getDatabase(databaseName);
		database.drop();
		projectCollection = database.getCollection(collectionName, Project.class);
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Test
	public void testGetAll_EmptyDatabase_ReturnsEmpty() {

		// Act
		List<Project> projectList = projectRepository.getAll();

		// Assert
		assertThat(projectList).isEmpty();
	}

	@Test
	public void testGetAll_DatabaseHasProjects_ReturnsProjects() {

		// Arrange
		Project project1 = new Project("1", "Desktop Application", "Desktop Application");
		Project project2 = new Project("2", "Web Application", "Web Application");
		projectCollection.insertMany(Arrays.asList(project1, project2));

		// Act
		List<Project> projectList = projectRepository.getAll();

		// Assert
		assertThat(projectList).hasSize(2);
		assertThat(projectList).containsExactly(new Project("1", "Desktop Application", "Desktop Application"),
				new Project("2", "Web Application", "Web Application"));
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {

		// Act
		Project project = projectRepository.findById("1");

		// Assert
		assertThat(project).isNull();
	}

	@Test
	public void testFindById_OnlyOneDatabaseEntry_EntryHasMatchingId_ReturnsProject() {

		// Arrange
		Project project1 = new Project("1", "Desktop Application", "Desktop Application");
		projectCollection.insertOne(project1);

		// Act
		Project project = projectRepository.findById("1");

		// Assert
		assertThat(project).isNotNull();
		assertThat(project).isEqualTo(new Project("1", "Desktop Application", "Desktop Application"));
	}

	@Test
	public void testFindById_ManyDatabaseEntries_OneOfThemHasMatchingId_ReturnsProject() {

		// Arrange
		Project project1 = new Project("1", "Desktop Application", "Desktop Application");
		Project project2 = new Project("2", "Web Application", "Web Application");
		projectCollection.insertMany(Arrays.asList(project1, project2));

		// Act
		Project project = projectRepository.findById("2");

		// Assert
		assertThat(project).isNotNull();
		assertThat(project).isEqualTo(new Project("2", "Web Application", "Web Application"));
	}

	@Test
	public void testSave_SavesProjectInTheDatabase() {

		// Arrange
		Project project1 = new Project("1", "Desktop Application", "Desktop Application");
		ArrayList<Project> databaseProjects = new ArrayList<Project>();

		// Act
		projectRepository.save(project1);

		// Assert
		projectCollection.find().into(databaseProjects);
		assertThat(databaseProjects).containsExactly(project1);
	}

	@Test
	public void testDelete_DeletesProjectFromTheDatabase() {

		// Arrange
		Project project1 = new Project("1", "Desktop Application", "Desktop Application");
		projectCollection.insertOne(project1);

		// Act
		projectRepository.delete(project1);
	}
}
