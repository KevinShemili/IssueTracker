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
import com.shemilikevin.app.tracker.model.Project;
import com.shemilikevin.app.tracker.repository.ProjectRepository;

public class ProjectMongoRepository implements ProjectRepository {

	private MongoCollection<Project> projectCollection;

	public ProjectMongoRepository(MongoClient mongoClient, String databaseName, String collectionName) {

		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		projectCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, Project.class);
	}

	@Override
	public List<Project> findAll() {

		return projectCollection.find().into(new ArrayList<Project>());
	}

	@Override
	public Project findById(String id) {

		return projectCollection.find(Filters.eq("id", id)).first();
	}

	@Override
	public void save(Project project) {

		projectCollection.insertOne(project);
	}

	@Override
	public void delete(String id) {

		projectCollection.deleteOne(Filters.eq("id", id));
	}

	@Override
	public boolean exists(String id) {

		Project project = projectCollection.find(Filters.eq("id", id)).first();

		return project == null ? false : true;
	}
}
