/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.dashboard.security.user.core.file;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.UserInfo;
import org.wso2.dashboard.security.user.core.common.AbstractUserStoreManager;
import org.wso2.micro.integrator.security.user.api.ClaimManager;
import org.wso2.micro.integrator.security.user.api.Permission;
import org.wso2.micro.integrator.security.user.api.Properties;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.UserStoreManager;
import org.wso2.micro.integrator.security.user.core.claim.Claim;
import org.wso2.micro.integrator.security.user.core.common.RoleContext;
import org.wso2.micro.integrator.security.user.core.tenant.Tenant;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;
import org.wso2.securevault.commons.MiscellaneousUtil;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class FileBasedUserStoreManager extends AbstractUserStoreManager {
    private static final Log log = LogFactory.getLog(FileBasedUserStoreManager.class);
    private static final FileBasedUserStoreManager userStoreManager = new FileBasedUserStoreManager();
    private static final String USER_MGT_CONFIG_FILE = "user-mgt.xml";
    private static final String REALM = "Realm";
    private static final String FILE_USER_STORE = "FileUserStore";
    private static final String USERS = "users";
    private static final String USER = "user";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IS_ADMIN = "isAdmin";
    private static Map<String, UserInfo> userMap;
    private static SecretResolver secretResolver;

    private FileBasedUserStoreManager() {
        initializeUserStore();
    }

    private static void initializeUserStore() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing FileBasedUserStoreManager");
        }
        OMElement documentElement = null;
        File userMgtConfigXml = new File(System.getProperty("carbon.config.dir.path"), USER_MGT_CONFIG_FILE);

        try (InputStream fileInputStream = Files.newInputStream(userMgtConfigXml.toPath())) {
            StAXOMBuilder builder = new StAXOMBuilder(fileInputStream);
            documentElement = builder.getDocumentElement();
        } catch (IOException | XMLStreamException e) {
            log.error("Error occurred while reading user-mgt.xml", e);
        }
        if (documentElement == null) {
            log.error("Error occurred while reading user-mgt.xml. Document element is null.");
            return;
        }

        secretResolver = SecretResolverFactory.create(documentElement, true);
        OMElement realmElement = documentElement.getFirstChildWithName(new QName(REALM));
        if (Objects.nonNull(realmElement)) {
            OMElement fileUserStore = realmElement.getFirstChildWithName(new QName(FILE_USER_STORE));
            if (Objects.nonNull(fileUserStore)) {
                userMap = populateUsers(fileUserStore.getFirstChildWithName(new QName(USERS)));
            } else {
                log.error("Error parsing the file based user store. File user store element not found in user-mgt.xml");
            }
        } else {
            log.error("Error parsing the file based user store. Realm element not found in user-mgt.xml");
        }
    }

    private static Map<String, UserInfo> populateUsers(OMElement users) {
        HashMap<String, UserInfo> userMap = new HashMap<>();
        if (users != null) {
            Iterator<OMElement> usersIterator = users.getChildrenWithName(new QName(USER));
            if (usersIterator != null) {
                while (usersIterator.hasNext()) {
                    OMElement userElement = usersIterator.next();
                    OMElement userNameElement = userElement.getFirstChildWithName(new QName(USERNAME));
                    OMElement passwordElement = userElement.getFirstChildWithName(new QName(PASSWORD));
                    OMElement isAdminElement = userElement.getFirstChildWithName(new QName(IS_ADMIN));

                    if (userNameElement != null && passwordElement != null) {
                        String userName = userNameElement.getText();
                        if (userMap.containsKey(userName)) {
                            System.out.println("Error parsing the file based user store. User: " + userName + " defined "
                                    + "more than once.");
                        }
                        boolean isAdmin = false;
                        if (isAdminElement != null) {
                            isAdmin = Boolean.parseBoolean(isAdminElement.getText().trim());
                        }
                        userMap.put(userName,
                                new UserInfo(resolveSecret(passwordElement.getText()).toCharArray(), isAdmin));
                    }
                }
            }
        }
        return userMap;
    }

    /**
     * Checks if the text is protected and returns decrypted text if protected, else returns the plain text
     *
     * @param text text to be resolved
     * @return Decrypted text if protected else plain text
     */
    private static String resolveSecret(String text) {
        String alias = MiscellaneousUtil.getProtectedToken(text);
        if (!StringUtils.isEmpty(alias)) {
            if (secretResolver.isInitialized()) {
                return MiscellaneousUtil.resolve(alias, secretResolver);
            }
        }
        return text;
    }

    /**
     * Method to retrieve FileBasedUserStoreManager
     *
     * @return FileBasedUserStoreManager
     */
    public static FileBasedUserStoreManager getUserStoreManager() {
        return userStoreManager;
    }

    @Override
    public boolean authenticate(final String userName, final Object credential) {
        if (userName == null || credential == null) {
            return false;
        }
        return doAuthenticate(userName, credential);
    }

    @Override
    protected boolean doAuthenticate(String userName, Object credential) {

        UserInfo userInfo = userMap.get(userName);
        if (userInfo != null) {
            return new String(userInfo.getPassword()).equals(credential);
        }
        return false;
    }

    @Override
    protected String[] doListUsers(String filter, int maxItemLimit) {
        return userMap.keySet().toArray(new String[0]);
    }

    @Override
    protected String[] doGetExternalRoleListOfUser(String s, String s1) {
        return new String[0];
    }

    @Override
    protected String[] doGetUserListOfRole(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUser(String s, Object o, String[] roles, Map<String, String> map, String s1, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doAddUser(String userName, Object credential, String[] roleList, Map<String, String> claims, String profileName, boolean requirePasswordChange) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDeleteUser(String userName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdateRoleListOfUser(String userName, String[] deletedRoles, String[] newRoles) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDeleteRole(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdateCredentialByAdmin(String userName, Object newCredential) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdateCredential(String userName, Object newCredential, Object oldCredential) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String[] doGetRoleNames(String filter, int maxItemLimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doCheckExistingRole(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doCheckExistingUser(String userName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected RoleContext createRoleContext(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doAddRole(String roleName, String[] userList) {

    }

    @Override
    public boolean doCheckIsUserInRole(String userName, String roleName) {
        return false;
    }

    @Override
    public boolean isExistingRole(String s, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRole(String s, String[] strings, Permission[] permissions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getProperties(org.wso2.micro.integrator.security.user.api.Tenant tenant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMultipleProfilesAllowed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRememberMe(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidRememberMeToken(String s, String s1) {
        return false;
    }

    @Override
    public ClaimManager getClaimManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSCIMEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getDefaultUserStoreProperties() {
        return null;
    }

    @Override
    public String[] getProfileNames(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUserClaimValue(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getUserClaimValues(String s, String[] strings, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Claim[] getUserClaimValues(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getAllProfileNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void addUser(String s, Object o, String[] strings, Map<String, String> map, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUserListOfRole(String s, String[] strings, String[] strings1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserClaimValue(String s, String s1, String s2, String s3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserClaimValues(String s, Map<String, String> map, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserClaimValue(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserClaimValues(String s, String[] strings, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getAllSecondaryRoles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getPasswordExpirationTime(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUserId(String s) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTenantId(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTenantId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getProperties(Tenant tenant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRoleName(String s, String s1) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean isBulkImportSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getUserList(String s, String s1, String s2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSecondaryUserStoreManager(UserStoreManager userStoreManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addSecondaryUserStoreManager(String s, UserStoreManager userStoreManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return this.realmConfig;
    }

    public boolean isAdmin(String username) {
        return userMap.get(username).isAdmin();
    }
}
