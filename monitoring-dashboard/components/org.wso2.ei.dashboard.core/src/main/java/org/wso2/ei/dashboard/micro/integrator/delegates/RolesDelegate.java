/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.dashboard.security.user.core.UserStoreManagerUtils;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.*;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;
import org.wso2.micro.integrator.security.user.api.UserStoreException;
import org.wso2.micro.integrator.security.user.api.UserStoreManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Delegate class to handle requests from roles page.
 */
public abstract class RolesDelegate {
    public static final String DESCENDING_ORDER = "desc";
    private static String prevSearchKey;
    private static List<RoleListInner> searchedList;
    private static int count;
    protected String groupId;

    RolesDelegate(String groupId) {
        this.groupId = groupId;
    }

    protected static String getPrevSearchKey() {
        return prevSearchKey;
    }

    protected static void setPrevSearchKey(String prevSearchKey) {
        RolesDelegate.prevSearchKey = prevSearchKey;
    }

    protected static List<RoleListInner> getSearchedList() {
        return searchedList;
    }

    protected static void setSearchedList(List<RoleListInner> searchedList) {
        RolesDelegate.searchedList = searchedList;
    }

    protected static int getCount() {
        return count;
    }

    protected static void setCount(int count) {
        RolesDelegate.count = count;
    }

    public static RolesDelegate getDelegate(String groupId) {
        return DelegatesUtil.isIcpManagement(groupId) ? new IcpRolesDelegate(groupId) : new MiRolesDelegate(groupId);
    }

    public abstract RolesResourceResponse fetchPaginatedRolesResponse(String searchKey,
                                                                      String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate)
            throws ManagementApiException, UserStoreException;

    public abstract RolesResourceResponse getAllRoles() throws ManagementApiException, UserStoreException;

    public abstract Ack addRole(AddRoleRequest request) throws ManagementApiException, UserStoreException;

    public abstract Ack updateRole(UpdateRoleRequest request) throws ManagementApiException, UserStoreException;

    public abstract Ack deleteRole(String roleName, String domain) throws ManagementApiException, UserStoreException;
}

class IcpRolesDelegate extends RolesDelegate {
    private static final Log log = LogFactory.getLog(IcpRolesDelegate.class);

    public IcpRolesDelegate(String groupId) {
        super(groupId);
    }

    @Override
    public RolesResourceResponse fetchPaginatedRolesResponse(String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) throws UserStoreException {
        if (UserStoreManagerUtils.isFileBasedUserStoreEnabled()) {
            throw new DashboardUserStoreException("Role management is not supported with the file-based user store. " +
                    "Please plug in a user store for the correct functionality", "403");

        }
        String resourceType = Constants.ROLES;
        DelegatesUtil.logDebugLogs(resourceType, groupId, lowerLimit, upperLimit, order, orderBy, isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        String prevSearchKey = getPrevSearchKey();
        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);
        List<RoleListInner> searchedList = getRoles(searchKey, order);
        setSearchedList(searchedList);
        setCount(searchedList.size());
        RoleList paginatedList = getPaginatedRolesResults(getSearchedList(), fromIndex, toIndex);
        RolesResourceResponse rolesResourceResponse = new RolesResourceResponse();
        rolesResourceResponse.setResourceList(paginatedList);
        rolesResourceResponse.setCount(getCount());
        setPrevSearchKey(searchKey);
        DelegatesUtil.setPrevResourceType(resourceType);
        return rolesResourceResponse;
    }

    @Override
    public RolesResourceResponse getAllRoles() throws UserStoreException {
        if (UserStoreManagerUtils.isFileBasedUserStoreEnabled()) {
            throw new DashboardUserStoreException("Role management is not supported with the file-based user store. " +
                    "Please plug in a user store for the correct functionality", "403");

        }
        RolesResourceResponse rolesResourceResponse = new RolesResourceResponse();
        String[] roles = UserStoreManagerUtils.getUserStoreManager().getRoleNames();
        RoleList roleList = new RoleList();
        for (String role : roles) {
            RoleListInner inner = new RoleListInner();
            inner.setRoleName(role);
            JsonArray jsonArray = new JsonArray();
            Arrays.stream(UserStoreManagerUtils.getUserStoreManager().getRoleListOfUser(role)).forEach(jsonArray::add);
            inner.setDetails(jsonArray.toString());
            roleList.add(inner);
        }
        rolesResourceResponse.setResourceList(roleList);
        rolesResourceResponse.setCount(roleList.size());
        return rolesResourceResponse;
    }

    @Override
    public Ack addRole(AddRoleRequest request) throws UserStoreException {
        UserStoreManager userStoreManager = UserStoreManagerUtils.getUserStoreManager();
        if (request.getRoleName() != null) {
            String role = request.getRoleName();
            if (userStoreManager.isExistingRole(role)) {
                throw new UserStoreException("The role : " + role + " already exists");
            }
            userStoreManager.addRole(role, null, null, false);
            return new Ack(Constants.SUCCESS_STATUS);
        } else {
            throw new UserStoreException("Missing role name in the payload");
        }
    }

    @Override
    public Ack updateRole(UpdateRoleRequest request) throws UserStoreException {
        UserStoreManager userStoreManager = UserStoreManagerUtils.getUserStoreManager();
        if (!userStoreManager.isExistingUser(request.getUserId())) {
            throw new UserStoreException("The user : " + request.getUserId() + " does not exists");
        }
        userStoreManager.updateRoleListOfUser(request.getUserId(), request.getRemovedRoles().toArray(new String[0]), request.getAddedRoles().toArray(new String[0]));
        return new Ack(Constants.SUCCESS_STATUS);
    }

    @Override
    public Ack deleteRole(String roleName, String domain) throws UserStoreException {
        if (UserStoreManagerUtils.isAdminRole(roleName)) {
            throw new UserStoreException("Cannot remove the admin role");
        }
        if (log.isDebugEnabled()) {
            log.debug("Requested details for the role: " + roleName);
        }
        UserStoreManager userStoreManager = UserStoreManagerUtils.getUserStoreManager();
        if (userStoreManager.isExistingRole(roleName)) {
            userStoreManager.deleteRole(roleName);
        } else {
            throw new UserStoreException("Role: " + roleName + " cannot be found.");
        }
        return new Ack(Constants.SUCCESS_STATUS);
    }

    private RoleList getPaginatedRolesResults(List<RoleListInner> roles, int lowerLimit, int upperLimit) {

        RoleList resultList = new RoleList();
        try {
            if (roles.size() < upperLimit) {
                upperLimit = roles.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<RoleListInner> paginatedList = roles.subList(lowerLimit, upperLimit);
            resultList.addAll(paginatedList);
            return resultList;
        } catch (IndexOutOfBoundsException e) {
            log.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal arguments for index values", e);
        }
        return null;
    }

    private List<RoleListInner> getRoles(String searchKey, String order) throws UserStoreException {
        String[] roles = UserStoreManagerUtils.getUserStoreManager().getRoleNames();
        RoleList roleList = new RoleList();
        for (String role : roles) {
            if (role.toLowerCase().contains(searchKey.toLowerCase())) {
                RoleListInner roleListInner = getRoleDetails(role);
                roleList.add(roleListInner);
            }
        }
        Comparator<RoleListInner> comparatorObject = Comparator.comparing(RoleListInner::getRoleName);
        if (DESCENDING_ORDER.equalsIgnoreCase(order)) {
            roleList.sort(comparatorObject.reversed());
        } else {
            roleList.sort(comparatorObject);
        }
        return roleList;
    }

    private static RoleListInner getRoleDetails(String role) throws UserStoreException {
        RoleListInner inner = new RoleListInner();
        inner.setRoleName(role);
        JsonObject rolesObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        Arrays.stream(UserStoreManagerUtils.getUserStoreManager().getUserListOfRole(role)).forEach(jsonArray::add);
        rolesObject.add("users", jsonArray);
        inner.setDetails(rolesObject.toString());
        return inner;
    }

}

class MiRolesDelegate extends RolesDelegate {
    private static final Log log = LogFactory.getLog(MiRolesDelegate.class);

    public MiRolesDelegate(String groupId) {
        super(groupId);
    }

    @Override
    public RolesResourceResponse fetchPaginatedRolesResponse(String searchKey, String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) throws ManagementApiException {
        String resourceType = Constants.ROLES;
        DelegatesUtil.logDebugLogs(resourceType, groupId, lowerLimit, upperLimit, order, orderBy, isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        String prevSearchKey = getPrevSearchKey();
        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);
        List<RoleListInner> searchedList = getSearchedRoles(groupId, searchKey, order);
        setSearchedList(searchedList);
        setCount(searchedList.size());
        RoleList paginatedList = getPaginatedRolesResultsFromMI(getSearchedList(), fromIndex, toIndex);
        RolesResourceResponse rolesResourceResponse = new RolesResourceResponse();
        rolesResourceResponse.setResourceList(paginatedList);
        rolesResourceResponse.setCount(getCount());
        setPrevSearchKey(searchKey);
        DelegatesUtil.setPrevResourceType(resourceType);
        return rolesResourceResponse;
    }

    @Override
    public RolesResourceResponse getAllRoles() throws ManagementApiException {
        RoleList roles = new RoleList();
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, roles of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/");
        JsonArray roleList = DelegatesUtil.getResourceResultList(groupId, nodeId, "roles",
                mgtApiUrl, accessToken, null);

        for (JsonElement role : roleList) {
            String roleId = role.getAsJsonObject().get("role").getAsString();
            if (!Objects.equals(roleId, Constants.INTERNAL_EVERYONE)) {
                RoleListInner roleListInner = getRoleDetails(groupId, nodeId, accessToken, url, roleId);
                roles.add(roleListInner);
            }
        }
        RolesResourceResponse rolesResourceResponse = new RolesResourceResponse();
        rolesResourceResponse.setResourceList(roles);
        rolesResourceResponse.setCount(roles.size());
        return rolesResourceResponse;
    }

    @Override
    public Ack addRole(AddRoleRequest request) throws ManagementApiException {
        log.debug("Adding role " + request.getRoleName() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddRolePayload(request);

        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles");
        CloseableHttpResponse httpResponse = Utils.doPost(groupId, nodeId, accessToken, url, payload);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }


    private JsonObject createAddRolePayload(AddRoleRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("role", request.getRoleName());
        String domain = request.getDomain();
        if (null != domain && !domain.isEmpty()) {
            payload.addProperty("domain", domain);
        }
        return payload;
    }

    @Override
    public Ack updateRole(UpdateRoleRequest request) throws ManagementApiException {
        log.debug("Updating roles of " + request.getUserId() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdatePayload(request);
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/");
        CloseableHttpResponse httpResponse = Utils.doPut(groupId, nodeId, accessToken, url, payload);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while updating role " + request.getUserId() + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
            return ack;
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private JsonObject createUpdatePayload(UpdateRoleRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", request.getUserId());
        payload.add("removedRoles", JsonParser.parseString(new Gson().toJson(request.getRemovedRoles())));
        payload.add("addedRoles", JsonParser.parseString(new Gson().toJson(request.getAddedRoles())));
        return payload;
    }

    @Override
    public Ack deleteRole(String roleName, String domain) throws ManagementApiException {
        if (StringUtils.isEmpty(domain)) {
            log.debug("Deleting role " + roleName + " in group " + groupId);
        } else {
            log.debug("Deleting role " + roleName + " in domain " + domain + " in group " + groupId);
        }
        Ack ack = new Ack(Constants.FAIL_STATUS);
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/").concat(roleName);
        if (!StringUtils.isEmpty(domain)) {
            url = url.concat("?domain=").concat(urlEncode(domain));
        }
        CloseableHttpResponse httpResponse = Utils.doDelete(groupId, nodeId, accessToken, url);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            log.error("Error occurred while deleting role " + roleName + " in group " + groupId);
            String message = HttpUtils.getJsonResponse(httpResponse).get("Error").getAsString();
            ack.setMessage(message);
            return ack;
        }
        ack.setStatus(Constants.SUCCESS_STATUS);
        return ack;
    }

    private RoleList getPaginatedRolesResultsFromMI(List<RoleListInner> roles, int lowerLimit, int upperLimit) {
        RoleList resultList = new RoleList();
        try {
            if (roles.size() < upperLimit) {
                upperLimit = roles.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<RoleListInner> paginatedList = roles.subList(lowerLimit, upperLimit);

            resultList.addAll(paginatedList);

            return resultList;

        } catch (IndexOutOfBoundsException e) {
            log.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal arguments for index values", e);
        }
        return null;
    }

    private List<RoleListInner> getSearchedRoles(String groupId, String searchKey,
                                                 String order) throws ManagementApiException {

        RoleList roles = new RoleList();
        NodeList nodeList = DataManagerSingleton.getDataManager().fetchNodes(groupId);
        // assumption - In a group, roles of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = DataManagerSingleton.getDataManager().getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/");
        JsonArray roleList = DelegatesUtil.getResourceResultList(groupId, nodeId, "roles",
                mgtApiUrl, accessToken, searchKey);

        for (JsonElement role : roleList) {
            String roleId = role.getAsJsonObject().get("role").getAsString();
            if (!Objects.equals(roleId, Constants.INTERNAL_EVERYONE)) {
                RoleListInner roleListInner = getRoleDetails(groupId, nodeId, accessToken, url, roleId);
                roles.add(roleListInner);
            }
        }
        Comparator<RoleListInner> comparatorObject;
        //for any other ordering options
        comparatorObject = Comparator.comparing(RoleListInner::getRoleName);
        if (DESCENDING_ORDER.equalsIgnoreCase(order)) {
            roles.sort(comparatorObject.reversed());
        } else {
            roles.sort(comparatorObject);
        }
        return roles;
    }

    private static RoleListInner getRoleDetails(String groupId, String nodeId, String accessToken, String url,
                                                String roleId) throws ManagementApiException {
        RoleListInner roleListInner = new RoleListInner();
        roleListInner.setRoleName(roleId);
        String getRoleDetailsUrl;
        String roleDetail;
        if (roleId.contains(Constants.DOMAIN_SEPARATOR)) {
            String[] parts = roleId.split(Constants.DOMAIN_SEPARATOR);
            getRoleDetailsUrl = url.concat(parts[1]).concat("?domain=").concat(urlEncode(parts[0]));
        } else {
            getRoleDetailsUrl = url.concat(roleId);
        }
        CloseableHttpResponse roleDetailResponse = Utils.doGet(groupId, nodeId, accessToken, getRoleDetailsUrl);
        roleDetail = HttpUtils.getStringResponse(roleDetailResponse);
        roleListInner.setDetails(roleDetail);
        return roleListInner;
    }

    private static String urlEncode(String userId) {
        try {
            return URLEncoder.encode(userId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Error occurred while encoding user id " + userId, e);
            return userId;
        }
    }
}
