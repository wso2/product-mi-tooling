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

import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.exception.DashboardServerException;

/**
 * Manage databases.
 */
public class DatabaseManagerFactory {

    private DatabaseManagerFactory() {

    }

    private static DatabaseManager databaseManager;

    public static DatabaseManager getDbManager() {
          if (databaseManager == null) {
              String connectionUrl = Constants.DATABASE_URL;
              String dbType = getDbType(connectionUrl);
              databaseManager = getDatabaseManager(dbType);
          }
          return databaseManager;
    }

    public static DatabaseManager getDatabaseManager(String dbType) {
        if ("jdbc".equals(dbType)) {
            return new JDBCDatabaseManager();
        }
        throw new DashboardServerException("The database type " + dbType + " is not supported.");
    }

   private static String getDbType(String connectionUrl) {
        String dbType = "";
        if (connectionUrl.startsWith("jdbc")) {
            dbType = "jdbc";
        }
        return dbType;
    }
}
