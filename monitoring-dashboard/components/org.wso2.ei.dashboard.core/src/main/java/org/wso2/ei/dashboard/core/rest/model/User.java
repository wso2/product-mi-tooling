/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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
 */
package org.wso2.ei.dashboard.core.rest.model;

/**
 * Sample class used to de-serialize JSON response from management API.
 */
public class User implements Comparable<User> {
    public String getUserId() {
        return userId;
    }

    private String userId;

    @Override
    public int compareTo(User o) {
        return this.userId.toLowerCase().compareTo(o.userId.toLowerCase());
    }
}
