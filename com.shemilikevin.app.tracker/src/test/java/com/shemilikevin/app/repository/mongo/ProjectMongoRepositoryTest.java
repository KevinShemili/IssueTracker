package com.shemilikevin.app.repository.mongo;

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
import com.shemilikevin.app.model.Project;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class ProjectMongoRepositoryTest {

	private static final String DATABASE_NAME = "db";
	private static final String COLLECTION_NAME = "collection";
	private static final String ID_1 = "1";
	private static final String ID_2 = "2";
	private static final String NAME_1 = "Desktop Application";
	private static final String NAME_2 = "Web Application";
	private static final String DESCRIPTION_1 = "Desktop Application Description";
	private static final String DESCRIPTION_2 = "Web Application Description";

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
	public void testGetAll_EmptyDatabase_ReturnsEmpty() {

		// Act
		List<Project> projectList = projectRepository.getAll();

		// Assert
		assertThat(projectList).isEmpty();
	}

	@Test
	public void testGetAll_DatabaseHasProjects_ReturnsProjects() {

		// Arrange
		addProjectToDb(new Project(ID_1, NAME_1, DESCRIPTION_1));
		addProjectToDb(new Project(ID_2, NAME_2, DESCRIPTION_2));

		// Act
		List<Project> projectList = projectRepository.getAll();

		// Assert
		assertThat(projectList).hasSize(2);
		assertThat(projectList).containsExactly(new Project(ID_1, NAME_1, DESCRIPTION_1),
				new Project(ID_2, NAME_2, DESCRIPTION_2));
	}

	@Test
	public void testFindById_NoMatchingIdInDatabase_ReturnsNull() {

		// Act
		Project project = projectRepository.findById(ID_1);

		// Assert
		assertThat(project).isNull();
	}

	@Test
	public void testFindById_OnlyOneDatabaseEntry_EntryHasMatchingId_ReturnsProject() {

		// Arrange
		addProjectToDb(new Project(ID_1, NAME_1, DESCRIPTION_1));

		// Act
		Project project = projectRepository.findById(ID_1);

		// Assert
		assertThat(project).isNotNull();
		assertThat(project).isEqualTo(new Project(ID_1, NAME_1, DESCRIPTION_1));
	}

	@Test
	public void testFindById_ManyDatabaseEntries_OneOfThemHasMatchingId_ReturnsProject() {

		// Arrange
		addProjectToDb(new Project(ID_1, NAME_1, DESCRIPTION_1));
		addProjectToDb(new Project(ID_2, NAME_2, DESCRIPTION_2));

		// Act
		Project project = projectRepository.findById(ID_2);

		// Assert
		assertThat(project).isNotNull();
		assertThat(project).isEqualTo(new Project(ID_2, NAME_2, DESCRIPTION_2));
	}

	@Test
	public void testSave_SavesProjectInTheDatabase() {

		// Act
		projectRepository.save(new Project(ID_1, NAME_1, DESCRIPTION_1));

		// Assert
		assertThat(queryAllProjectsFromDb()).containsExactly(new Project(ID_1, NAME_1, DESCRIPTION_1));
	}

	@Test
	public void testDelete_DeletesProjectFromTheDatabase() {

		// Arrange
		addProjectToDb(new Project(ID_1, NAME_1, DESCRIPTION_1));

		// Act
		projectRepository.delete(ID_1);

		// Assert
		assertThat(queryAllProjectsFromDb()).isEmpty();
	}

	private void addProjectToDb(Project project) {
		projectCollection.insertOne(project);
	}

	private List<Project> queryAllProjectsFromDb() {
		return projectCollection.find().into(new ArrayList<Project>());
	}
}
