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

public class DatabaseQueryObject {

    private String tableName;

    private String[] insertColumns;
    private String[] insertValues;

    private String[] selectColumns;
    private String selectCondition;

    private String deleteCondition;

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }


    public String[] getInsertColumns() {

        return insertColumns;
    }

    public void setInsertColumns(String[] insertColumns) {

        this.insertColumns = insertColumns;
    }

    public String[] getInsertValues() {

        return insertValues;
    }

    public void setInsertValues(String[] insertValues) {

        this.insertValues = insertValues;
    }

    public String[] getSelectColumns() {

        return selectColumns;
    }

    public void setSelectColumns(String[] selectColumns) {

        this.selectColumns = selectColumns;
    }

    public String getSelectCondition() {

        return selectCondition;
    }

    public void setSelectCondition(String selectCondition) {

        this.selectCondition = selectCondition;
    }

    public String getDeleteCondition() {

        return deleteCondition;
    }

    public void setDeleteCondition(String deleteCondition) {

        this.deleteCondition = deleteCondition;
    }
}
