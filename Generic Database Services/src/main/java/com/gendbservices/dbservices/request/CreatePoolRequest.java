package com.gendbservices.dbservices.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePoolRequest {

    private String dbName;
    private String poolType;
    private String driver;
    private String host;
    private String userId;
    private String password;
    private String poolName;
    private String uuid;
    private String connectionTestQuery = "SELECT 1 FROM DUAL";

    private boolean testIdle = true;
    private boolean testBorrow = true;
    private boolean logAbandoned = true;
    private boolean tcpKeepAlive = true;
    private boolean cachePrepStmts = true;
    private boolean userServerPrepStmts = true;

    private int maxActive = 80;
    private int minimumIdle = 0;
    private int idleTimeout = 30000;
    private int leakDetectionThreshold = 60*1000;
    private int prepStmtCacheSize = 2560;
    private int prepStmtCacheSqlLimit = 20480;

    private HashMap<String, String> additionProps;

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        if (additionProps != null) {
            for (Map.Entry<String, String> entry : this.additionProps.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
            }
        }
        return "CreatePoolRequest [type = " + dbName + ", driver = " + driver + ", host = " + host + ", userId = " + userId
                + ", password = " + password + ", poolName = " + poolName + ", uuid = " + uuid + ",connectionTestQuery = " + connectionTestQuery
                + ", testIdle  = " +  testIdle + ", testBorrow = " + testBorrow + ", logAbandoned = " + logAbandoned + ", tcpKeepAlive = " + tcpKeepAlive
                + ", cachePrepStmts = " + cachePrepStmts + ", userServerPrepStmts = " + userServerPrepStmts + ", maxActive = " + maxActive
                + ", minimumIdle = " + minimumIdle + ", idleTimeout = " + idleTimeout + ", leakDetectionThreshold = " + leakDetectionThreshold
                + ", prepStmtCacheSize =  " + prepStmtCacheSize + ", prepStmtCacheSqlLimit = " + prepStmtCacheSqlLimit
                + ", additionProps = " + additionProps + "]";
    }
}
