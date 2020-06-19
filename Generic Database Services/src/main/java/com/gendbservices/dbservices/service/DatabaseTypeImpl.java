package com.gendbservices.dbservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gendbservices.dbservices.servicedaoimpl.DatabaseTypeDaoImpl;
import com.gendbservices.dbservices.exception.GenDbException;
import com.gendbservices.dbservices.request.CreatePoolRequest;
import com.gendbservices.dbservices.request.QueryRequest;
import com.gendbservices.dbservices.response.QueryResponse;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DatabaseTypeImpl implements DatabaseType {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTypeImpl.class);

    @Autowired
    EncryptorDecryptor encryption;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    DatabaseTypeDaoImpl databaseTypeDaoImpl;
    @Resource(name = "uuidPool")
    Map<String, Object> poolMap;

    @Override
    public ResponseEntity<Map<String, String>> createConnectionPool(CreatePoolRequest createPoolRequest, boolean recreate)
            throws GenDbException, JsonProcessingException {

        logger.info("Create connection pool impl");
        Map<String, String> response = new HashMap<>();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(createPoolRequest.getHost());
        hikariConfig.setUsername(createPoolRequest.getUserId());
        hikariConfig.setPassword(createPoolRequest.getPassword());
        hikariConfig.setPoolName(createPoolRequest.getPoolName());
        hikariConfig.setConnectionTestQuery(createPoolRequest.getConnectionTestQuery());
        hikariConfig.setMaximumPoolSize(createPoolRequest.getMaxActive());
        hikariConfig.setMinimumIdle(createPoolRequest.getMinimumIdle());
        hikariConfig.setIdleTimeout(createPoolRequest.getIdleTimeout());
        hikariConfig.setLeakDetectionThreshold(createPoolRequest.getLeakDetectionThreshold());
        Map<String, String> additionParams = createPoolRequest.getAdditionProps();
        if (additionParams !=null){
            for(Map.Entry<String, String> entry: additionParams.entrySet()){
                hikariConfig.addDataSourceProperty(entry.getKey(), entry.getValue());
            }
        }
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        String uuid = null;
        if(!recreate){
            uuid = UUID.randomUUID().toString();
            createPoolRequest.setPassword(encryption.aesEncrypt(createPoolRequest.getPassword()));
            String jsonRequest = objectMapper.writeValueAsString(createPoolRequest);
            //Database insertion
            databaseTypeDaoImpl.insertPoolObject(encryption.aesEncrypt(uuid), jsonRequest);
        }else {
            uuid = createPoolRequest.getUuid();
        }
        //saving pool object in map
        poolMap.put(uuid, hikariDataSource);
        response.put("uuid", uuid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public QueryResponse executeQuery(QueryRequest queryRequest) throws GenDbException {

        QueryResponse queryResponse = null;
        Boolean result = false;
        logger.info("Query request with uuid: {}", queryRequest.getUuid());

        if (poolMap.containsKey(queryRequest.getUuid())){
            try (Connection connection = ((HikariDataSource)poolMap.get(queryRequest.getUuid())).getConnection()){
                String query = queryRequest.getQuery();
                logger.info("Query: {}", query);
                if (query.toUpperCase().contains("SELECT")) {
                    ResultSet resultSet = connection.createStatement().executeQuery(query);
                    queryResponse = databaseTypeDaoImpl.getQueryResults(resultSet);
                }else if(query.toUpperCase().contains("INSERT")
                      || query.toUpperCase().contains("REPLACE")
                      || query.toUpperCase().contains("UPDATE")
                      || query.toUpperCase().contains("DELETE")
                      || query.toUpperCase().contains("CREATE")){
                    result = connection.createStatement().execute(query);
                }else {
                    throw new GenDbException("Supported query: Insert, Update, Replace, Delete and Select" +
                            " Unsupported query: drop/create aren't supported");
                }
            } catch (SQLException sqlException) {
                throw new GenDbException(sqlException.getMessage());
            }
        }else {
            throw new GenDbException("Invalid/Unavailable uuid, please try with correct credentials");
        }
        return queryResponse;
    }
}
