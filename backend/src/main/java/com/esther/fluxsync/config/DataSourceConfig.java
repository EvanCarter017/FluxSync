package com.esther.fluxsync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.esther.fluxsync.ds.RoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean("dsFluxsyncMain")
    @ConfigurationProperties(prefix = "app.datasource.fluxsync")
    public DataSource dsFluxsyncMain() { return DataSourceBuilder.create().build(); }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("dsFluxsyncMain") DataSource ds1
                                 ) {
        RoutingDataSource routing = new RoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();

        // Source Mapper
        targets.put("fluxsync", ds1);

        routing.setTargetDataSources(targets);
        routing.setDefaultTargetDataSource(ds1);    // default database
        return routing;
    }

    @Bean
    public DataSourceTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
