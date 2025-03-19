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
 */

package org.wso2.ei.dashboard.micro.integrator.delegates;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.config.mapper.ConfigParser;
import org.wso2.dashboard.security.user.core.UserStoreManagerUtils;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.dashboard.security.user.core.common.DataHolder;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.AddUserRequest;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.PasswordRequest;
import org.wso2.ei.dashboard.core.rest.model.User;
import org.wso2.ei.dashboard.core.rest.model.Users;
import org.wso2.ei.dashboard.core.rest.model.UsersInner;
import org.wso2.ei.dashboard.core.rest.model.UsersResourceResponse;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.api.UserStoreException;
import org.wso2.micro.integrator.security.user.api.UserStoreManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.wso2.ei.dashboard.core.commons.Constants.*;

/**
 * Delegate class to handle requests from users page.
 */
public abstract class UsersDelegate {
    public static final String DESCENDING_ORDER = "desc";
    private static User[] users;
    private static String previousSearchKey;
    private static int userCount;
    protected String groupId;

    UsersDelegate(String groupId) {
        this.groupId = groupId;
    }

    protected static synchronized User[] getAllUserIds() {
        return users;
    }

    protected static synchronized void setAllUserIds(User[] userIds) {
        users = userIds;
    }

    protected static synchronized String getPreviousSearchKey() {
        return previousSearchKey;
    }

    protected static synchronized void setPreviousSearchKey(String searchKey) {
        previousSearchKey = searchKey;
    }

    protected static synchronized int getUserCount() {
        return userCount;
    }

    protected static synchronized void setUserCount(int count) {
        userCount = count;
    }

    public static UsersDelegate getDelegate(String groupId) {
        return DelegatesUtil.isIcpManagement(groupId) ? new IcpUsersDelegate(groupId) : new MiUsersDelegate(groupId);
    }

    public abstract UsersResourceResponse fetchPaginatedUsers(String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) throws UserStoreException, ManagementApiException;

    public abstract Ack addUser(AddUserRequest request) throws ManagementApiException, UserStoreException;

    public abstract Ack updateUserPassword(PasswordRequest request, String accessToken, String performedBy) throws ManagementApiException, UserStoreException;

    public abstract Ack deleteUser(String userId, String domain, String performedBy) throws ManagementApiException, UserStoreException;
}

class IcpUsersDelegate extends UsersDelegate {
    private static final Log log = LogFactory.getLog(UsersDelegate.class);
    private static final String SUPER_ADMIN_USERNAME_PROPERTY = "super_admin.username";

    public IcpUsersDelegate(String groupId) {
        super(groupId);
    }

    @Override
    public UsersResourceResponse fetchPaginatedUsers(String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) throws UserStoreException {
        if (UserStoreManagerUtils.isFileBasedUserStoreEnabled()) {
            throw new DashboardUserStoreException("User management is not supported with the file-based user store. " +
                    "Please plug in a user store for the correct functionality", "403");

        }
        DelegatesUtil.logDebugLogs(USERS, groupId, lowerLimit, upperLimit, order, orderBy, isUpdate);
        String prevSearchKey = getPreviousSearchKey();
        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);
        String searchPattern = "*".concat(searchKey).concat("*");
        User[] users = getSearchedUsers(searchPattern);
        Arrays.sort(users);
        setAllUserIds(users);
        setUserCount(users.length);
        
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        Users paginatedUsers = getPaginatedUsersResult(getAllUserIds(), fromIndex, toIndex, order);

        UsersResourceResponse response = new UsersResourceResponse();
        response.setResourceList(paginatedUsers);
        response.setCount(getUserCount());

        // Update previous state for tracking
        setPreviousSearchKey(searchKey);
        DelegatesUtil.setPrevResourceType(USERS);
        return response;
    }

    @Override
    public Ack addUser(AddUserRequest request) throws UserStoreException {
        log.debug("Adding user " + request.getUserId() + " to " + groupId);
        UserStoreManager manager = UserStoreManagerUtils.getUserStoreManager();
        synchronized (this) {
            String[] roleList = request.isIsAdmin() ? new String[]{"admin"} : new String[]{};
            manager.addUser(request.getUserId(), request.getPassword(), roleList, null, null, false);
        }
        return new Ack(SUCCESS_STATUS);
    }

    @Override
    public Ack updateUserPassword(PasswordRequest request, String accessToken, String performedBy) throws UserStoreException {
        if (UserStoreManagerUtils.isFileBasedUserStoreEnabled()) {
            throw new DashboardUserStoreException("Unable update password with the file-based user store. " +
                    "Please plug in a user store for the correct functionality", "403");
        }
        String user = request.getUserId();
        if (log.isDebugEnabled()) {
            log.debug("Request received to update user credentials: " + user);
        }
        if (Objects.isNull(performedBy)) {
            log.warn(
                    "Update a user without authenticating/authorizing the request sender. Adding "
                            + "authentication and authorization handlers is recommended.");
        }
        if (request.getNewPassword() != null && request.getConfirmPassword() != null) {
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmPassword();
            String oldPassword = request.getOldPassword();
            if (newPassword.equals(confirmPassword)) {
                UserStoreManager userStoreManager = UserStoreManagerUtils.getUserStoreManager();
                String superAdminUserName = (String) ConfigParser.getParsedConfigs().get(SUPER_ADMIN_USERNAME_PROPERTY);
                synchronized (this) {
                    String[] userRoles = userStoreManager.getRoleListOfUser(user);
                    String[] performerRoles = userStoreManager.getRoleListOfUser(performedBy);
                    if (user.equals(performedBy)) {
                        if (oldPassword == null) {
                            throw new UserStoreException("The current user password cannot be null.");
                        }
                        if (user.equals(superAdminUserName)) {
                            throw new UserStoreException("Super admin is not allowed to update credentials.");
                        }
                        userStoreManager.updateCredential(user, newPassword, oldPassword);
                    } else if (superAdminUserName.equals(performedBy)) {
                        userStoreManager.updateCredentialByAdmin(user, newPassword);
                    } else if (Arrays.asList(performerRoles).contains(ADMIN) &&
                            !Arrays.asList(userRoles).contains(ADMIN)) {
                        userStoreManager.updateCredentialByAdmin(user, newPassword);
                    } else if (Arrays.asList(performerRoles).contains(ADMIN) &&
                            Arrays.asList(userRoles).contains(ADMIN)) {
                        throw new UserStoreException("Only a super admin user can update the credentials of another admin.");
                    } else {
                        throw new UserStoreException("Only your own credentials can be updated by a user.");
                    }
                }
            } else {
                throw new UserStoreException("New password and re-typed password does not match.");
            }
        } else {
            throw new UserStoreException("New password or re-typed password is missing in the payload.");
        }
        return new Ack(SUCCESS_STATUS);
    }

    @Override
    public Ack deleteUser(String userId, String domain, String performedBy) throws UserStoreException {
        if (log.isDebugEnabled()) {
            log.debug("Request received to delete the user: " + userId + "by user: " + performedBy);
        }
        if (Objects.isNull(performedBy)) {
            log.warn("Deleting a user without authenticating/authorizing the request sender. Adding "
                    + "authentication and authorization handlers is recommended.");
        } else {
            if (performedBy.equals(userId)) {
                throw new IllegalArgumentException(
                        "Attempt to delete the logged in user. Operation not allowed. Please login "
                                + "from another user.");
            }
        }
        UserStoreManager userStoreManager = UserStoreManagerUtils.getUserStoreManager();
        String[] roles = userStoreManager.getRoleListOfUser(userId);

        RealmConfiguration realmConfig = DataHolder.getInstance().getRealmConfig();
        String superAdmin = realmConfig.getAdminRoleName();
        if (superAdmin != null && superAdmin.equals(performedBy)) {
            userStoreManager.deleteUser(userId);
        } else if (!Arrays.asList(roles).contains(ADMIN)) {
            userStoreManager.deleteUser(userId);
        } else {
            log.error("Only super admin user can delete admins");
            throw new UserStoreException("Only super admin user can delete admins");
        }
        return new Ack(SUCCESS_STATUS);
    }

    private Users getPaginatedUsersResult(User[] userArray, int lowerLimit, int upperLimit,
                                          String order) throws UserStoreException {
        try {
            upperLimit = Math.min(userArray.length, upperLimit);
            lowerLimit = Math.min(lowerLimit, upperLimit);
            User[] paginatedUsersArray = Arrays.copyOfRange(userArray, lowerLimit, upperLimit);
            Users users = queryUserInfo(paginatedUsersArray);
            if (DESCENDING_ORDER.equalsIgnoreCase(order)) {
                Collections.reverse(users);
            }
            return users;
        } catch (IndexOutOfBoundsException e) {
            log.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal arguments for index values", e);
        }
        return null;
    }

    private static Users queryUserInfo(User[] users) throws UserStoreException {
        Users resultList = new Users();
        for (User user : users) {
            String[] roles = UserStoreManagerUtils.getUserStoreManager().getRoleListOfUser(user.getUserId());

            JsonObject userDetails = new JsonObject();
            userDetails.addProperty(USER_ID, user.getUserId());
            userDetails.addProperty(IS_ADMIN, UserStoreManagerUtils.isAdminUser(user.getUserId()));

            JsonArray rolesArray = new JsonArray();
            Arrays.stream(roles).forEach(rolesArray::add);
            userDetails.add(ROLES, rolesArray);

            UsersInner usersInner = new UsersInner();
            usersInner.userId(user.getUserId());
            usersInner.setDetails(userDetails.toString());

            resultList.add(usersInner);
        }
        Collections.sort(resultList);
        return resultList;
    }

    private static User[] getSearchedUsers(String searchKey) throws UserStoreException {
        return Arrays.stream(UserStoreManagerUtils.getUserStoreManager().listUsers(searchKey, -1))
                .map(User::new).toArray(User[]::new);
    }
}

class MiUsersDelegate extends UsersDelegate {
    private static final Log log = LogFactory.getLog(UsersDelegate.class);

    public MiUsersDelegate(String groupId) {
        super(groupId);
    }

    @Override
    public UsersResourceResponse fetchPaginatedUsers(String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) throws ManagementApiException {
        DelegatesUtil.logDebugLogs(USERS, groupId, lowerLimit, upperLimit, order, orderBy, isUpdate);
        String prevSearchKey = getPreviousSearchKey();
        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);

        User[] users = getSearchedUsers(groupId, searchKey);
        Arrays.sort(users);
        setAllUserIds(users);
        setUserCount(users.length);

        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        Users paginatedUsers = getPaginatedUsersResultsFromMI(getAllUserIds(), fromIndex, toIndex, groupId, order);

        UsersResourceResponse response = new UsersResourceResponse();
        response.setResourceList(paginatedUsers);
        response.setCount(getUserCount());

        // Update previous state for tracking
        setPreviousSearchKey(searchKey);
        DelegatesUtil.setPrevResourceType(USERS);
        return response;
    }

    private static User[] getSearchedUsers(String groupId, String searchKey) throws ManagementApiException {
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, users of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        JsonArray usersList = DelegatesUtil.getResourceResultList(groupId, nodeId, "users", mgtApiUrl,
                accessToken, searchKey);
        return new Gson().fromJson(usersList, User[].class);
    }

    private Users getPaginatedUsersResultsFromMI(User[] users, int lowerLimit, int upperLimit, String groupId, String order) throws ManagementApiException {
        Users resultList = new Users();
        try {
            upperLimit = Math.min(users.length, upperLimit);
            lowerLimit = Math.min(lowerLimit, upperLimit);
            users = Arrays.copyOfRange(users, lowerLimit, upperLimit);

            // creating the URL and fetch role info of user in the current page
            fetchUserInfo(users, groupId, resultList);
            Collections.sort(resultList);

            if (DESCENDING_ORDER.equalsIgnoreCase(order)) {
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
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String url = mgtApiUrl.concat("users/");
        for (User user : users) {
            UsersInner usersInner = getUserDetails(groupId, nodeId, url, user.getUserId());
            resultList.add(usersInner);
        }
    }

    private static UsersInner getUserDetails(String groupId, String nodeId, String url, String userId) throws ManagementApiException {
        UsersInner usersInner = new UsersInner();
        usersInner.setUserId(userId);
        String getUsersDetailsUrl;
        if (userId.contains(DOMAIN_SEPARATOR)) {
            String[] parts = userId.split(DOMAIN_SEPARATOR);
            getUsersDetailsUrl = url.concat(urlEncode(parts[1])).concat("?domain=").concat(urlEncode(parts[0]));
        } else {
            getUsersDetailsUrl = url.concat(urlEncode(userId));
        }
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        try (CloseableHttpResponse userDetailResponse = Utils.doGet(groupId, nodeId, accessToken, getUsersDetailsUrl)) {
            String userDetail = HttpUtils.getStringResponse(userDetailResponse);
            usersInner.setDetails(userDetail);
            return usersInner;
        } catch (IOException e) {
            throw new ManagementApiException("Error while retrieving user details", 500);
        }
    }

    private static String urlEncode(String userId) {
        try {
            return URLEncoder.encode(userId, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error("Error occurred while encoding user id " + userId, e);
            return userId;
        }
    }

    @Override
    public Ack addUser(AddUserRequest request) throws ManagementApiException {
        log.debug("Adding user " + request.getUserId() + " in group " + groupId);
        Ack ack = new Ack(FAIL_STATUS);
        JsonObject payload = createAddUserPayload(request);

        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("users");
        CloseableHttpResponse response = null;
        try {
            response = Utils.doPost(groupId, nodeId, accessToken, url, payload);
            ack.setStatus(SUCCESS_STATUS);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    log.error("Error closing the http response", e);
                }
            }
        }
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

    @Override
    public Ack updateUserPassword(PasswordRequest request, String accessToken, String performedBy)
            throws ManagementApiException {
        Ack ack = new Ack(FAIL_STATUS);
        JsonObject payload = createUserUpdatePasswordPayload(request);

        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String userId = request.getUserId();
        String url = mgtApiUrl.concat("users/");
        if (userId.contains(DOMAIN_SEPARATOR)) {
            String[] parts = userId.split(DOMAIN_SEPARATOR);
            // parts[0] = domain, parts[1] = userId
            url = url.concat(urlEncode(parts[1])).concat("?domain=").concat(urlEncode(parts[0]));
        } else {
            url = url.concat(urlEncode(userId));
        }
        try (CloseableHttpResponse ignored = Utils.doPatch(groupId, nodeId, accessToken, url, payload)) {
            ack.setStatus(SUCCESS_STATUS);
        } catch (IOException e) {
            log.error("Error closing the http response. ", e);
        }
        return ack;
    }

    private JsonObject createUserUpdatePasswordPayload(PasswordRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("newPassword", request.getNewPassword());
        payload.addProperty("confirmPassword", request.getConfirmPassword());
        payload.addProperty("oldPassword", request.getOldPassword());
        return payload;
    }

    @Override
    public Ack deleteUser(String userId, String domain, String performedBy) throws ManagementApiException {
        if (StringUtils.isEmpty(domain)) {
            log.debug("Deleting user " + userId + " in group " + groupId);
        } else {
            log.debug("Deleting user " + userId + " in domain " + domain + " in group " + groupId);
        }
        Ack ack = new Ack(FAIL_STATUS);
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("users/").concat(urlEncode(userId));
        if (!StringUtils.isEmpty(domain)) {
            url = url.concat("?domain=").concat(urlEncode(domain));
        }
        try (CloseableHttpResponse httpResponse = Utils.doDelete(groupId, nodeId, accessToken, url)) {
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                log.error("Error occurred while deleting user " + userId + " in group " + groupId);
                String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
                ack.setMessage(message);
                return ack;
            }
            ack.setStatus(SUCCESS_STATUS);
            return ack;
        } catch (IOException e) {
            throw new ManagementApiException("Error while deleting user", 500);
        }
    }
}
