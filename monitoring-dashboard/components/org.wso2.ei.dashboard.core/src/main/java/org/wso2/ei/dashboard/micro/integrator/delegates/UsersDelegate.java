/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.ei.dashboard.micro.integrator.delegates;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.AddUserRequest;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.User;
import org.wso2.ei.dashboard.core.rest.model.Users;
import org.wso2.ei.dashboard.core.rest.model.UsersInner;
import org.wso2.ei.dashboard.core.rest.model.UsersResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.Arrays;
import java.util.Collections;

/**
 * Delegate class to handle requests from users page.
 */
public class UsersDelegate {
    private static final Log log = LogFactory.getLog(UsersDelegate.class);
    private static final DataManager dataManager = DataManagerSingleton.getDataManager();
    private static User[] allUserIds;
    private static String prevSearchKey = null;
    private static int count;


    public UsersResourceResponse fetchPaginatedUsers(String groupId, String searchKey, 
        String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        log.debug("Fetching Searched Users from MI.");
        log.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        log.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);

        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);

        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey))) {
            allUserIds = getSearchedUsers(groupId, searchKey);
            Arrays.sort(allUserIds);
            count = allUserIds.length;
        }
        Users paginatedUsers = getPaginatedUsersResultsFromMI(allUserIds, fromIndex, toIndex, groupId, order, orderBy);
        UsersResourceResponse usersResourceResponse = new UsersResourceResponse();
        usersResourceResponse.setResourceList(paginatedUsers);
        usersResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        return usersResourceResponse;
    }

    public Ack addUser(String groupId, AddUserRequest request) throws ManagementApiException {
        log.debug("Adding user " + request.getUserId() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddUserPayload(request);

        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("users");
        Utils.doPost(groupId, nodeId, accessToken, url, payload);
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    public Ack deleteUser(String groupId, String userId, String domain) throws ManagementApiException {
        if (StringUtils.isEmpty(domain)) {
            log.debug("Deleting user " + userId + " in group " + groupId);
        } else {
            log.debug("Deleting user " + userId + " in domain " + domain + " in group " + groupId);
        }
        Ack ack = new Ack(Constants.FAIL_STATUS);
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("users/").concat(userId);
        if (!StringUtils.isEmpty(domain)) {
            url = url.concat("?domain=").concat(domain);
        }
        CloseableHttpResponse httpResponse = Utils.doDelete(groupId, nodeId, accessToken, url);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while deleting user " + userId + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
            return ack;
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private JsonObject createAddUserPayload(AddUserRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", request.getUserId());
        String domain = request.getDomain();
        if (!StringUtils.isEmpty(domain)) {
            payload.addProperty("domain", domain);
        }
        payload.addProperty("password", request.getPassword());
        payload.addProperty("isAdmin", request.isIsAdmin().toString());
        return payload;
    }

    private static User[] getSearchedUsers(String groupId, String searchKey) throws ManagementApiException {

        Users users = new Users();
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, users of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        JsonArray usersList = DelegatesUtil.getResourceResultList(groupId, nodeId, "users", 
                mgtApiUrl, accessToken, searchKey);
        return new Gson().fromJson(usersList, User[].class);
    }

    private Users getPaginatedUsersResultsFromMI(User[] users, int lowerLimit, int upperLimit, String groupId,
                                                 String order, String orderBy) throws ManagementApiException {

        Users resultList = new Users();
        try {
            if (users.length < upperLimit) {
                upperLimit = users.length;
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            users = Arrays.copyOfRange(users, lowerLimit, upperLimit);

            // creating the URL and fetch role info of user in the current page
            fetchUserInfo(users, groupId, resultList);
            Collections.sort(resultList);

            if ("desc".equalsIgnoreCase(order)) {
                Collections.reverse(resultList);
            }
            return resultList;

        } catch (IndexOutOfBoundsException e) {
            log.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal arguments for index values", e);
        }
        return null;      
    }

    /**
     * Fetch individual user details only for the user of the current page.
     *
     * @param users      all users
     * @param groupId    groupId
     * @param resultList list of user details
     * @throws ManagementApiException error occurred while fetching user details.
     */
    private void fetchUserInfo(User[] users, String groupId, Users resultList)
            throws ManagementApiException {
        NodeList nodeList = dataManager.fetchNodes(groupId);
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("users/");
        for (User user : users) {
            UsersInner usersInner = getUserDetails(groupId, nodeId, accessToken, url, user.getUserId());
            resultList.add(usersInner);
        }
    }

    private static UsersInner getUserDetails(String groupId, String nodeId, String accessToken, String url,
                                             String userId)throws ManagementApiException {
        UsersInner usersInner = new UsersInner();
        usersInner.setUserId(userId);
        String getUsersDetailsUrl;
        if (userId.contains(Constants.DOMAIN_SEPARATOR)) {
            String[] parts = userId.split(Constants.DOMAIN_SEPARATOR);
            // parts[0] = domain, parts[1] = new userId
            getUsersDetailsUrl = url.concat(parts[1]).concat("?domain=").concat(parts[0]);
        } else {
            getUsersDetailsUrl = url.concat(userId);
        }
        CloseableHttpResponse userDetailResponse = Utils.doGet(groupId, nodeId, accessToken, getUsersDetailsUrl);
        String userDetail = HttpUtils.getStringResponse(userDetailResponse);
        usersInner.setDetails(userDetail);
        return usersInner;
    }
}
