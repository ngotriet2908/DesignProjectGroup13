//package com.group13.tcsprojectgrading.config;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.nimbusds.oauth2.sdk.util.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.mongodb.*;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
//import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
//import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableMongoRepositories(basePackages = "com.group13.tcsprojectgrading.repositories")
//public class MongoConfig extends AbstractMongoClientConfiguration{
//    @Value("${spring.data.mongodb.host}")
//    private String mongoHost;
//
//    @Value("${spring.data.mongodb.port}")
//    private String mongoPort;
//
//    @Value("${spring.data.mongodb.database}")
//    private String mongoDB;
//
//
//    @Bean
//    MongoTransactionManager transactionManager(@Qualifier("mongoDbFactory") MongoDatabaseFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
//
//    @Bean
//    public MongoDatabaseFactory mongoDbFactory() {
//        return new SimpleMongoClientDatabaseFactory("mongodb://"+ mongoHost + ":" + mongoPort + "/" + mongoDB
//        + "?replicaSet=rs0");
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return "test";
//    }
//
////    @Override
////    public MongoClient mongoClient() {
////        final ConnectionString connectionString = new ConnectionString("mongodb://"+ mongoHost + ":" + mongoPort + "/" + mongoDB);
////        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
////                .applyConnectionString(connectionString)
////                .build();
////        return MongoClients.create(mongoClientSettings);
////    }
//}
