package com.mdymen.skiplocked.consumer.datasource.origin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
//
//@Configuration
//@EnableJpaRepositories(
//        basePackages = {"com.mdymen.skiplocked.consumer.datasource.origin"}
//)
//@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class OriginDataSourceConfiguration {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean
    @Primary
    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }



}
