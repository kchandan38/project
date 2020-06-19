package com.gendbservices.dbservices.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gendbservices.dbservices.servicedaoimpl.DatabaseTypeDaoImpl;
import com.gendbservices.dbservices.exception.GenDbException;
import com.gendbservices.dbservices.request.CreatePoolRequest;
import com.gendbservices.dbservices.service.DatabaseTypeImpl;
import com.gendbservices.dbservices.service.EncryptorDecryptor;
import com.hazelcast.core.HazelcastInstance;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@Configuration
@ComponentScan(basePackages = {"com.gendbservices.dbservices.*", "com.gendbservices.dbservices"})
public class DatabaseBeanConfig {

    @Autowired
    private DatabaseClusterConfig databaseClusterConfig;
    private static int maxActive = 80;
    public static HazelcastInstance hazelcastInstance;
    @Autowired
    DatabaseTypeDaoImpl databaseTypeDaoImpl;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EncryptorDecryptor encDec;
    @Autowired
    DatabaseTypeImpl service;

    @Bean(name="uuidPool")
    public Map<String, Object> poolMap(){
        Map<String, Object> disCacheMap = new HashMap<>();
        return disCacheMap;
    }

    @Bean
    public HikariDataSource dbConnection() throws Exception {
        Map<String, String> databaseConfiguration = databaseClusterConfig.getDbConfig();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(databaseConfiguration.get("driver"));
        hikariConfig.setJdbcUrl(databaseConfiguration.get("host"));
        hikariConfig.setUsername(databaseConfiguration.get("userId"));
        hikariConfig.setPassword(databaseConfiguration.get("password"));
        hikariConfig.setPoolName(databaseConfiguration.get("poolName"));
        hikariConfig.setMaximumPoolSize(maxActive);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setLeakDetectionThreshold(60 * 1000);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 2560);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 20480);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }

    //Construct pool map for queries avail in mariaDb
    @PostConstruct
    public void init() throws SQLException, GenDbException, JsonProcessingException {

        Map<String, String> poolConfig = databaseTypeDaoImpl.getPoolConfig();

        for (Map.Entry<String, String> entry: poolConfig.entrySet()){

            CreatePoolRequest createPoolRequest = objectMapper.readValue(entry.getValue(), CreatePoolRequest.class);
            createPoolRequest.setUuid(entry.getKey());
            createPoolRequest.setPassword(encDec.aesDecrypt(createPoolRequest.getPassword()));
            createPoolRequest.setUuid(encDec.aesDecrypt(createPoolRequest.getUuid()));
            service.createConnectionPool(createPoolRequest, true);
        }
    }
}
