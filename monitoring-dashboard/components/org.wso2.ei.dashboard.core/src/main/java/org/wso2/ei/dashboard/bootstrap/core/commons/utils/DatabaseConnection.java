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

package org.wso2.ei.dashboard.bootstrap.core.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.ei.dashboard.bootstrap.core.commons.Constants;
import org.wso2.ei.dashboard.bootstrap.core.db.manager.DatabaseManager;
import org.wso2.ei.dashboard.bootstrap.core.db.manager.DatabaseManagerFactory;
import org.wso2.ei.dashboard.bootstrap.core.exception.DashboardServerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Log log = LogFactory.getLog(DatabaseConnection.class);

    private static DatabaseManager databaseManager;

    public static DatabaseManager getDbManager() {
          if (databaseManager == null) {
              String connectionUrl = Constants.DATABASE_URL;
              String dbType = DbUtils.getDbType(connectionUrl);
              if (dbType.equals("h2")) {
                  createInMemoryDB();
              }
              DatabaseManagerFactory databaseManagerFactory = new DatabaseManagerFactory();
              databaseManager = databaseManagerFactory.getDatabaseManager(dbType);
          }
          return databaseManager;
    }

    private static void createInMemoryDB() {
        String scriptLocation = Constants.DASHBOARD_HOME + "/dbscripts/h2.sql";
        String dbUrl = Constants.DATABASE_URL + ";INIT=RUNSCRIPT FROM '" + scriptLocation + "'";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new DashboardServerException("Error occurred while creating in memory database");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Failed to close database connection.", e);
                }
            }
        }
    }
}
