package com.gendbservices.dbservices.controller;

import com.gendbservices.dbservices.exception.GenDbException;
import com.gendbservices.dbservices.request.CreatePoolRequest;
import com.gendbservices.dbservices.request.QueryRequest;
import com.gendbservices.dbservices.response.QueryResponse;
import com.gendbservices.dbservices.service.DatabaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@ComponentScan(basePackages = {"com.gendbservices.dbservices.*", "com.gendbservices.dbservices"})
@RequestMapping(value = "api/genDatabaseServices")
public class DatabaseServiceController {

    @Resource(name = "uuidPool")
    Map<String, Object> poolMap;
    @Autowired
    DatabaseType service;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceController.class);
    Set<String> poolTypes = new HashSet<>(Arrays.asList("HICAKRI", "DBCP"));
    Set<String> dbNames = new HashSet<>(Arrays.asList("MARIADB","ORACLEDB", "TERADATADB","SQLSERVER"));
    StringBuilder stringBuilder = new StringBuilder();

    // To create the pool and persist in database to use in future
    @PostMapping (value="/createPool", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createPool(@RequestBody CreatePoolRequest createPoolRequest) throws Exception{

        logger.info("Create pool connection uuid");
        ResponseEntity<Map<String, String>> responseEntity = null;
        logger.info(String.valueOf(poolTypes.contains(createPoolRequest.getPoolType().toUpperCase())));

        if(poolTypes.contains(createPoolRequest.getPoolType().toUpperCase())
                && dbNames.contains(createPoolRequest.getDbName().toUpperCase())){
            logger.info("Inside create connection pool method");
            responseEntity = service.createConnectionPool(createPoolRequest, false);
        }else {
            stringBuilder.append("Available poolTypes = ");
            for (String poolTypesName:poolTypes){
                stringBuilder.append(poolTypesName).append(", ");
            }
            stringBuilder.append("Available DbName = ");
            for (String dbName: dbNames){
                stringBuilder.append(dbName).append(", ");
            }
            throw new GenDbException(stringBuilder.toString());
        }
        return responseEntity;
    }
    //To get the query result from database
    @PostMapping(value = "/queryResponse", produces = MediaType.APPLICATION_JSON_VALUE)
    public QueryResponse queryResponse(@RequestBody QueryRequest queryRequest) throws GenDbException{

        QueryResponse response = null;
        logger.info("Inside execute query");
        response = service.executeQuery(queryRequest);
        return response;

    }

}
