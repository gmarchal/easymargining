package com.easymargining.replication.eurex.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * Created by Gilles Marchal on 18/01/2016.
 */
@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabaseName;

    @Override
    protected String getDatabaseName() {
        return mongoDatabaseName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient();
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.easymargining.replication.eurex.domain.repository";
    }
}
