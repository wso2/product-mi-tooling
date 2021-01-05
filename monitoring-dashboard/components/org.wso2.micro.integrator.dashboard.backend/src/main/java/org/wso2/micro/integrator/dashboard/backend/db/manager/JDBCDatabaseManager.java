/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.wso2.micro.integrator.dashboard.backend.db.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.micro.integrator.dashboard.backend.commons.Constants;
import org.wso2.micro.integrator.dashboard.backend.exception.DashboardServerException;
import org.wso2.micro.integrator.dashboard.backend.rest.model.HeatbeatSignalRequestBody;

import javax.sql.DataSource;

public final class JDBCDatabaseManager implements DatabaseManager {

    private static final Log log = LogFactory.getLog(JDBCDatabaseManager.class);
    private final DataSource ds;

    JDBCDatabaseManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty(Constants.DATABASE_URL));
        config.setUsername(System.getProperty(Constants.DATABASE_USERNAME));
        config.setPassword(System.getProperty(Constants.DATABASE_PASSWORD));

        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource(config);
    }

    @Override
    public int updateDatabase(String query) {
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(query);
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while updating data using query : " + query, e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int insertHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        String query = "INSERT INTO HEARTBEAT VALUES ('" + heartbeat.getGroupId() + "','" + heartbeat.getNodeId()
                       + "'," + heartbeat.getInterval() + ",CURRENT_TIMESTAMP());";
        return updateDatabase(query);
    }

    @Override
    public int updateHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        String query = "UPDATE HEARTBEAT SET TIMESTAMP=CURRENT_TIMESTAMP() WHERE GROUP_ID='" + heartbeat.getGroupId()
                       + "' AND NODE_ID='" + heartbeat.getNodeId() + "';";
        return updateDatabase(query);
    }

    @Override
    public int deleteHeartbeat(HeatbeatSignalRequestBody heartbeat) {
        String query = "DELETE FROM HEARTBEAT WHERE GROUP_ID='" + heartbeat.getGroupId() + "' AND NODE_ID='"
                       + heartbeat.getNodeId() + "';";
        return updateDatabase(query);
    }

    public String retrieveTimestampOfHeartBeat(HeatbeatSignalRequestBody heartbeat) {
        String query = "SELECT TIMESTAMP FROM HEARTBEAT WHERE GROUP_ID='" + heartbeat.getGroupId() + "' AND NODE_ID='"
                       + heartbeat.getNodeId() + "';";
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(query);
            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while retrieveTimestampOfRegisteredNode results.", e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean checkIfTimestampExceedsInitial(HeatbeatSignalRequestBody heartbeat, String initialTimestamp) {
        String query = "SELECT COUNT(*) FROM HEARTBEAT WHERE TIMESTAMP>'" + initialTimestamp + "' AND GROUP_ID='"
                       + heartbeat.getGroupId() + "' AND NODE_ID='" + heartbeat.getNodeId() + "';";
        boolean isExists = false;
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement(query);
            ResultSet resultSet = pst.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 1) {
                isExists = true;
            }
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while retrieving next row.", e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isExists;
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
