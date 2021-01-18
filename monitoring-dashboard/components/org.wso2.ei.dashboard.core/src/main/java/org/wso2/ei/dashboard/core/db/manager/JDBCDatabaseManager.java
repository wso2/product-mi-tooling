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

package org.wso2.ei.dashboard.core.db.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;
import org.wso2.ei.dashboard.core.rest.delegates.heartbeat.HeartbeatObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Performs jdbc operations.
 */
public final class JDBCDatabaseManager implements DatabaseManager {

    private static final Log log = LogFactory.getLog(JDBCDatabaseManager.class);
    private final DataSource dataSource;

    public JDBCDatabaseManager() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(Constants.DATABASE_URL);
        config.setUsername(Constants.DATABASE_USERNAME);
        config.setPassword(Constants.DATABASE_PASSWORD);

        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty("prepStmtCacheSize" , "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        dataSource = new HikariDataSource(config);
    }

    @Override
    public boolean insertHeartbeat(HeartbeatObject heartbeat) {
        String query = "INSERT INTO HEARTBEAT VALUES (?,?,?,CURRENT_TIMESTAMP());";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            statement.setInt(3, heartbeat.getInterval());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while inserting heartbeat information.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public boolean insertServerInformation(HeartbeatObject heartbeat, String serverInfo) {
        String query = "INSERT INTO SERVERS VALUES (?,?,?);";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            statement.setString(3, serverInfo);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while inserting server information of node : "
                                               + heartbeat.getNodeId() + " in group: " + heartbeat.getGroupId(), e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public boolean insertProxyServices(HeartbeatObject heartbeat, String serviceName, String details) {
        String query = "INSERT INTO PROXY_SERVICES VALUES (?,?,?,?);";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            statement.setString(3, serviceName);
            statement.setString(4, details);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while inserting " + serviceName
                                               + " proxy information.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public boolean insertApis(HeartbeatObject heartbeat, String apiName, String details) {
        String query = "INSERT INTO APIS VALUES (?,?,?,?);";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            statement.setString(3, apiName);
            statement.setString(4, details);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while inserting " + apiName + " api information.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public boolean updateHeartbeat(HeartbeatObject heartbeat) {
        String query = "UPDATE HEARTBEAT SET TIMESTAMP=CURRENT_TIMESTAMP() WHERE GROUP_ID=? AND NODE_ID=?;";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while updating heartbeat information.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public int deleteHeartbeat(HeartbeatObject heartbeat) {
        String query = "DELETE FROM HEARTBEAT WHERE GROUP_ID=? AND NODE_ID=?;";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while updating heartbeat information.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    public String retrieveTimestampOfHeartBeat(HeartbeatObject heartbeat) {
        String query = "SELECT TIMESTAMP FROM HEARTBEAT WHERE GROUP_ID = ? AND NODE_ID = ? ;";
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, heartbeat.getGroupId());
            statement.setString(2, heartbeat.getNodeId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while retrieveTimestampOfRegisteredNode results.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
    }

    @Override
    public boolean checkIfTimestampExceedsInitial(HeartbeatObject heartbeat, String initialTimestamp) {
        String query = "SELECT COUNT(*) FROM HEARTBEAT WHERE TIMESTAMP>? AND GROUP_ID=? AND NODE_ID=?;";
        boolean isExists = false;
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = getConnection();
            statement = con.prepareStatement(query);
            statement.setString(1, initialTimestamp);
            statement.setString(2, heartbeat.getGroupId());
            statement.setString(3, heartbeat.getNodeId());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 1) {
                isExists = true;
            }
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while retrieving next row.", e);
        } finally {
            closeStatement(statement);
            closeConnection(con);
        }
        return isExists;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void closeStatement(PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            log.error("Error occurred while closing the statement.", e);
        }
    }

    private void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            log.error("Error occurred while closing the connection.", e);
        }
    }
}
