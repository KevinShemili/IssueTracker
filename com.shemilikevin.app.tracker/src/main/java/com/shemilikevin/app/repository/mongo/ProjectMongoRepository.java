package com.shemilikevin.app.repository.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.shemilikevin.app.model.Project;

public class ProjectMongoRepository {

	private MongoCollection<Project> projectCollection;

	public ProjectMongoRepository(MongoClient mongoClient, String databaseName, String collectionName) {

		CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		projectCollection = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
				.getCollection(collectionName, Project.class);
	}

	public List<Project> getAll() {

		List<Project> projectList = new ArrayList<Project>();
		projectCollection.find().into(projectList);

		return projectList;
	}

	public Project findById(String id) {

		Project project = projectCollection.find(Filters.eq("id", id)).first();

		return project;
	}

	public void save(Project project1) {
		// TODO Auto-generated method stub

	}
}
