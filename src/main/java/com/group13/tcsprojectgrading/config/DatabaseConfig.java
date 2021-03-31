//package com.group13.tcsprojectgrading.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//public class DatabaseConfig {
//
//
//    @Bean
//    public PlatformTransactionManager txManager(@Autowired DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//}
