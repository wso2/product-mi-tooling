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
 *
 *
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
import org.wso2.ei.dashboard.core.commons.Constants;
import org.wso2.ei.dashboard.core.commons.utils.HttpUtils;
import org.wso2.ei.dashboard.core.commons.utils.ManagementApiUtils;
import org.wso2.ei.dashboard.core.data.manager.DataManager;
import org.wso2.ei.dashboard.core.data.manager.DataManagerSingleton;
import org.wso2.ei.dashboard.core.exception.ManagementApiException;
import org.wso2.ei.dashboard.core.rest.model.Ack;
import org.wso2.ei.dashboard.core.rest.model.AddRoleRequest;
import org.wso2.ei.dashboard.core.rest.model.NodeList;
import org.wso2.ei.dashboard.core.rest.model.RoleList;
import org.wso2.ei.dashboard.core.rest.model.RoleListInner;
import org.wso2.ei.dashboard.core.rest.model.RolesResourceResponse;
import org.wso2.ei.dashboard.core.rest.model.UpdateRoleRequest;
import org.wso2.ei.dashboard.micro.integrator.commons.DelegatesUtil;
import org.wso2.ei.dashboard.micro.integrator.commons.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Delegate class to handle requests from roles page.
 */
public class RolesDelegate {
    private static final Log log = LogFactory.getLog(RolesDelegate.class);
    private static final DataManager dataManager = DataManagerSingleton.getDataManager();
    private static List<RoleListInner>  searchedList;
    private static String prevSearchKey = null;
    private static int count;

    public RolesResourceResponse fetchPaginatedRolesResponse(String groupId, String searchKey, 
        String lowerLimit, String upperLimit, String order, String orderBy, String isUpdate) 
        throws ManagementApiException {
        log.debug("Fetching Searched Roles from MI.");
        log.debug("group id :" + groupId + ", lowerlimit :" + lowerLimit + ", upperlimit: " + upperLimit);
        log.debug("Order:" + order + ", OrderBy:" + orderBy + ", isUpdate:" + isUpdate);
        int fromIndex = Integer.parseInt(lowerLimit);
        int toIndex = Integer.parseInt(upperLimit);
        boolean isUpdatedContent = Boolean.parseBoolean(isUpdate);
        log.debug("prevSearch key :" + prevSearchKey + ", currentSearch key:" + searchKey);
        if (isUpdatedContent || prevSearchKey == null || !(prevSearchKey.equals(searchKey))) {
            searchedList = getSearchedRoles(groupId, searchKey, order, orderBy);
            count = searchedList.size();
        }
        RoleList paginatedList = getPaginatedRolesResultsFromMI(searchedList, fromIndex, toIndex);
        RolesResourceResponse rolesResourceResponse = new RolesResourceResponse();
        rolesResourceResponse.setResourceList(paginatedList);
        rolesResourceResponse.setCount(count);
        prevSearchKey = searchKey;
        return rolesResourceResponse;
    }

    private  List<RoleListInner> getSearchedRoles(String groupId, String searchKey, 
        String order, String orderBy) throws ManagementApiException {

        RoleList roles = new RoleList();
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, roles of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
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
        switch (orderBy.toLowerCase()) {
            //for any other ordering options
            default: comparatorObject = Comparator.comparing(RoleListInner::getRoleName); break;
        }
        if ("desc".equalsIgnoreCase(order)) {
            Collections.sort(roles, comparatorObject.reversed());
        } else {
            Collections.sort(roles, comparatorObject);
        }

        return roles;
    }

    public RolesResourceResponse getAllRoles(String groupId) throws ManagementApiException {

        RoleList roles = new RoleList();
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, roles of all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
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

    private RoleList getPaginatedRolesResultsFromMI(List<RoleListInner> roles, int lowerLimit, int upperLimit) 
        throws ManagementApiException {
        
        RoleList resultList = new RoleList();
        try {
            if (roles.size() < upperLimit) {
                upperLimit = roles.size();
            }
            if (upperLimit < lowerLimit) {
                lowerLimit = upperLimit;
            }
            List<RoleListInner> paginatedList = roles.subList(lowerLimit, upperLimit);
        
            for (RoleListInner roleListInner : paginatedList) {
                resultList.add(roleListInner);
            }
            
            return resultList;

        } catch (IndexOutOfBoundsException e) {
            log.error("Index values are out of bound", e);
        } catch (IllegalArgumentException e) {
            log.error("Illegal arguments for index values", e);
        }
        return null;      
    }

    public Ack addRole(String groupId, AddRoleRequest request) throws ManagementApiException {
        log.debug("Adding role " + request.getRoleName() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createAddRolePayload(request);

        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles");
        CloseableHttpResponse httpResponse = Utils.doPost(groupId, nodeId, accessToken, url, payload);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            ack.setStatus(Constants.SUCCESS_STATUS);
        }
        return ack;
    }

    public Ack updateRole(String groupId, UpdateRoleRequest request) throws ManagementApiException {
        log.debug("Updating roles of " + request.getUserId() + " in group " + groupId);
        Ack ack = new Ack(Constants.FAIL_STATUS);
        JsonObject payload = createUpdatePayload(request);
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
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

    public Ack deleteRole(String groupId, String roleName, String domain) throws ManagementApiException {
        if (StringUtils.isEmpty(domain)) {
            log.debug("Deleting role " + roleName + " in group " + groupId);
        } else {
            log.debug("Deleting role " + roleName + " in domain " + domain + " in group " + groupId);
        }
        Ack ack = new Ack(Constants.FAIL_STATUS);
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, all nodes use a shared user-store
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/").concat(roleName);
        if (!StringUtils.isEmpty(domain)) {
            url = url.concat("?domain=").concat(domain);
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

    private RoleList getRoles(String groupId) throws ManagementApiException {
        RoleList roleList = new RoleList();
        NodeList nodeList = dataManager.fetchNodes(groupId);
        // assumption - In a group, roles in all nodes in the group should be identical
        String nodeId = nodeList.get(0).getNodeId();
        String mgtApiUrl = ManagementApiUtils.getMgtApiUrl(groupId, nodeId);
        String accessToken = dataManager.getAccessToken(groupId, nodeId);
        String url = mgtApiUrl.concat("roles/");
        CloseableHttpResponse httpResponse = Utils.doGet(groupId, nodeId, accessToken, url);
        JsonArray roles = HttpUtils.getJsonResponse(httpResponse).get("list").getAsJsonArray();
        for (JsonElement role : roles) {
            String roleId = role.getAsJsonObject().get("role").getAsString();
            if (!Objects.equals(roleId, Constants.INTERNAL_EVERYONE)) {
                RoleListInner roleListInner = getRoleDetails(groupId, nodeId, accessToken, url, roleId);
                roleList.add(roleListInner);
            }
        }
        return roleList;
    }

    private static RoleListInner getRoleDetails(String groupId, String nodeId, String accessToken, String url,
                                         String roleId) throws ManagementApiException {
        RoleListInner roleListInner = new RoleListInner();
        roleListInner.setRoleName(roleId);
        String getRoleDetailsUrl;
        String roleDetail;
        if (roleId.contains(Constants.DOMAIN_SEPARATOR)) {
            String[] parts = roleId.split(Constants.DOMAIN_SEPARATOR);
            // parts[0] = domain, parts[1] = new roleId
            getRoleDetailsUrl = url.concat(parts[1]).concat("?domain=").concat(parts[0]);
        } else {
            getRoleDetailsUrl = url.concat(roleId);
        }
        CloseableHttpResponse roleDetailResponse = Utils.doGet(groupId, nodeId, accessToken, getRoleDetailsUrl);
        roleDetail = HttpUtils.getStringResponse(roleDetailResponse);
        roleListInner.setDetails(roleDetail);
        return roleListInner;
    }

    private JsonObject createAddRolePayload(AddRoleRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("role", request.getRoleName());
        String domain = request.getDomain();
        if (null != domain && !domain.equals("")) {
            payload.addProperty("domain", domain);
        }
        return payload;
    }

    private JsonObject createUpdatePayload(UpdateRoleRequest request) {
        JsonObject payload = new JsonObject();
        payload.addProperty("userId", request.getUserId());
        payload.add("removedRoles", JsonParser.parseString(new Gson().toJson(request.getRemovedRoles())));
        payload.add("addedRoles", JsonParser.parseString(new Gson().toJson(request.getAddedRoles())));
        return payload;
    }
}
