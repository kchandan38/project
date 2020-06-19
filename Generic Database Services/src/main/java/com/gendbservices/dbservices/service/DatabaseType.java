package com.gendbservices.dbservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gendbservices.dbservices.exception.GenDbException;
import com.gendbservices.dbservices.request.CreatePoolRequest;
import com.gendbservices.dbservices.request.QueryRequest;
import com.gendbservices.dbservices.response.QueryResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DatabaseType {

    ResponseEntity<Map<String, String>> createConnectionPool(CreatePoolRequest createPoolRequest, boolean recreate)
            throws GenDbException, JsonProcessingException;

    QueryResponse executeQuery(QueryRequest queryRequest) throws GenDbException;
}
