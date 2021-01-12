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

package org.wso2.ei.dashboard.core.commons;

public final class Constants {

    private Constants() {

    }

    public static final String DASHBOARD_HOME = System.getenv("DASHBOARD_HOME");
    public static final String HEARTBEAT_POOL_SIZE = System.getProperty("heartbeat_pool_size");
    public static final String DATABASE_URL = "jdbc:h2:mem:ei-dashboard-db;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '"
            + Constants.DASHBOARD_HOME + "/dbscripts/h2.sql'";
    public static final String DATABASE_USERNAME = System.getProperty("db_username");
    public static final String DATABASE_PASSWORD = System.getProperty("db_password");

}
