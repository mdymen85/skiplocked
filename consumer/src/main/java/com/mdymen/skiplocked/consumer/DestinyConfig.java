package com.mdymen.skiplocked.consumer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "destinyEntityManagerFactory",
        transactionManagerRef = "destinyTransactionManager",
        basePackages = { "com.mdymen.skiplocked.consumer.datasource.destiny" })
public class DestinyConfig {

    @Bean(name="destinyProps")
    @ConfigurationProperties("destiny")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name="destinyDatasource")
    public DataSource datasource(@Qualifier("destinyProps") DataSourceProperties properties){
        return properties.initializeDataSourceBuilder().build();
    }


    @Bean(name="destinyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (EntityManagerFactoryBuilder builder,
             @Qualifier("destinyDatasource") DataSource dataSource){
        return builder.dataSource(dataSource)
                .packages("com.mdymen.skiplocked.consumer.datasource.destiny")
                .persistenceUnit("Destiny").build();
    }


    @Bean(name = "destinyTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("destinyEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}