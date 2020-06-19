package com.gendbservices.dbservices.servicedaoimpl;

import com.gendbservices.dbservices.configuration.DatabaseServiceConstants;
import com.gendbservices.dbservices.controller.DatabaseServiceController;
import com.gendbservices.dbservices.exception.GenDbException;
import com.gendbservices.dbservices.response.QueryResponse;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DatabaseTypeDaoImpl {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceController.class);

    @Autowired
    private HikariDataSource hikariDataSource;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-DD-YYYY hh:mm:ss");

    public void insertPoolObject(String uuid, String jsonRequest) throws GenDbException {

        logger.info("Inserting pool connection for uuid: {}", uuid);
        try(Connection connection = hikariDataSource.getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DatabaseServiceConstants.INSERT_POOL_DATA)){

            Long requiredTime = System.currentTimeMillis();
            Date resultDate = new Date(requiredTime);
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, jsonRequest);
            preparedStatement.setString(3, String.valueOf(resultDate));
            logger.info("Prepared Statement Insert: {}", preparedStatement);
            preparedStatement.executeUpdate();
            logger.info("Pool has been created successfully");

        } catch (SQLException sqlException) {
            logger.error("Exception while inserting record into table: {}", sqlException.getMessage());
            throw new GenDbException(sqlException.getMessage(), sqlException.getCause());
        }
    }

    public QueryResponse getQueryResults(ResultSet resultSet) throws GenDbException, SQLException {

        QueryResponse response = new QueryResponse(0, new ArrayList<List<Object>>());
         int numberColumn = resultSet.getMetaData().getColumnCount();
         logger.info("Number of columns for given request: {}", numberColumn);
         int rows = 0;
         if(resultSet!=null){
             try{
                 while (resultSet.next()){
                     ++rows;
                     List<Object> record = new ArrayList<>();
                     for (int i = 1; i <= numberColumn; i++){
                         record.add(resultSet.getString(i));
                     }
                     response.addRecord(record);
                 }
             }catch (SQLException sqlException){
                 throw  new GenDbException(sqlException.getMessage());
             }
         }
         logger.info("Number of rows in response: {}", rows);
         response.setRecordCount(rows);
         return response;
    }

    public Map<String, String> getPoolConfig() throws GenDbException{

        Map<String, String> uuidRequest = new HashMap<>();
        try(Connection connection = hikariDataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(DatabaseServiceConstants.GET_POOL_DATA)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String uuid = resultSet.getString(1);
                String poolJsonRequest = resultSet.getString(2);
                uuidRequest.put(uuid, poolJsonRequest);
            }
            resultSet.close();
        }catch (SQLException sqlException) {
            throw new GenDbException("Exception while extracting request from db " + sqlException.getMessage());
        }
        return uuidRequest;
    }
}
