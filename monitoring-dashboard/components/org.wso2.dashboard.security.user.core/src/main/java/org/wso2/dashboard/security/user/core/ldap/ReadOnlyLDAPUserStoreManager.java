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

package org.wso2.dashboard.security.user.core.ldap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.security.user.core.common.AbstractUserStoreManager;
import org.wso2.dashboard.security.user.core.DatabaseUtil;
import org.wso2.dashboard.security.user.core.common.DashboardUserStoreException;
import org.wso2.dashboard.security.user.core.common.Secret;
import org.wso2.dashboard.security.user.core.common.UnsupportedSecretTypeException;
import org.wso2.micro.integrator.security.user.api.Permission;
import org.wso2.micro.integrator.security.user.api.Properties;
import org.wso2.micro.integrator.security.user.api.RealmConfiguration;
import org.wso2.micro.integrator.security.user.core.UserCoreConstants;
import org.wso2.micro.integrator.security.user.core.UserStoreException;
import org.wso2.micro.integrator.security.user.core.UserStoreManager;
import org.wso2.micro.integrator.security.user.core.claim.Claim;
import org.wso2.micro.integrator.security.user.core.claim.ClaimManager;
import org.wso2.micro.integrator.security.user.core.common.RoleContext;
import org.wso2.micro.integrator.security.user.core.ldap.LDAPConstants;
import org.wso2.micro.integrator.security.user.core.profile.ProfileConfigurationManager;
import org.wso2.micro.integrator.security.user.core.tenant.Tenant;
import org.wso2.micro.integrator.security.user.core.util.JNDIUtil;

import javax.cache.Cache;
import javax.cache.CacheBuilder;
import javax.cache.CacheManager;
import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadOnlyLDAPUserStoreManager extends AbstractUserStoreManager {
    public static final String MEMBER_UID = "memberUid";
    protected static final int MAX_USER_CACHE = 200;
    protected static final int MEMBERSHIP_ATTRIBUTE_RANGE_VALUE = 0;
    //Authenticating to LDAP via Anonymous Bind
    private static final String USE_ANONYMOUS_BIND = "AnonymousBind";
    private static Log log = LogFactory.getLog(ReadOnlyLDAPUserStoreManager.class);
    protected CacheManager cacheManager;
    protected LDAPConnectionContext connectionSource = null;
    protected String userSearchBase = null;
    protected String groupSearchBase = null;
    /*
     * following is by default true since embedded-ldap allows it. If connected
     * to an external ldap
     * where empty roles not allowed, then following property should be set
     * accordingly in
     * user-mgt.xml
     */
    protected boolean emptyRolesAllowed = false;
    /**
     * The use of this Map is Deprecated. Please use userDnCache.
     * Retained so that any extended class will function as it used to be.
     */
    @Deprecated
    Map<String, Object> userCache = new ConcurrentHashMap<>(MAX_USER_CACHE);
    private String cacheExpiryTimeAttribute = ""; //Default: expire with default system wide cache expiry
    private CacheBuilder userDnCacheBuilder = null; //Use cache manager if not null to get cache
    private String userDnCacheName;
    private boolean userDnCacheEnabled = true;

    public ReadOnlyLDAPUserStoreManager() {

    }

    /**
     * This operates in the pure read-only mode without a connection to a
     * database. No handling of
     * Internal roles.
     */
    public ReadOnlyLDAPUserStoreManager(RealmConfiguration realmConfig, ClaimManager claimManager,
                                        ProfileConfigurationManager profileManager) throws UserStoreException {

        if (log.isDebugEnabled()) {
            log.debug("Started " + System.currentTimeMillis());
        }
        this.realmConfig = realmConfig;

        // check if required configurations are in the user-mgt.xml
        checkRequiredUserStoreConfigurations();

        this.connectionSource = new LDAPConnectionContext(realmConfig);

        try {
            this.dataSource = DatabaseUtil.getRealmDataSource(realmConfig);
        } catch (Exception ex) {
            // datasource is not configured
            log.error("Datasource is not configured for LDAP user store: ", ex);
        }
    }

    /**
     * @throws UserStoreException
     */
    protected void checkRequiredUserStoreConfigurations() throws UserStoreException {

        log.debug("Checking LDAP configurations ");

        String connectionURL = realmConfig.getUserStoreProperty(LDAPConstants.CONNECTION_URL);
        String DNSURL = realmConfig.getUserStoreProperty(LDAPConstants.DNS_URL);
        String AnonymousBind = realmConfig.getUserStoreProperty(USE_ANONYMOUS_BIND);

        if ((connectionURL == null || connectionURL.trim().length() == 0) &&
                ((DNSURL == null || DNSURL.trim().length() == 0))) {
            throw new UserStoreException(
                    "Required ConnectionURL property is not set at the LDAP configurations");
        }
        if (!Boolean.parseBoolean(AnonymousBind)) {
            String connectionName = realmConfig.getUserStoreProperty(LDAPConstants.CONNECTION_NAME);
            if (StringUtils.isEmpty(connectionName)) {
                throw new UserStoreException(
                        "Required ConnectionNme property is not set at the LDAP configurations");
            }
            String connectionPassword =
                    realmConfig.getUserStoreProperty(LDAPConstants.CONNECTION_PASSWORD);
            if (StringUtils.isEmpty(connectionPassword)) {
                throw new UserStoreException(
                        "Required ConnectionPassword property is not set at the LDAP configurations");
            }
        }
        userSearchBase = realmConfig.getUserStoreProperty(LDAPConstants.USER_SEARCH_BASE);
        if (userSearchBase == null || userSearchBase.trim().length() == 0) {
            throw new UserStoreException(
                    "Required UserSearchBase property is not set at the LDAP configurations");
        }
        String usernameListFilter =
                realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_LIST_FILTER);
        if (usernameListFilter == null || usernameListFilter.trim().length() == 0) {
            throw new UserStoreException(
                    "Required UserNameListFilter property is not set at the LDAP configurations");
        }

        String usernameSearchFilter =
                realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_SEARCH_FILTER);
        if (usernameSearchFilter == null || usernameSearchFilter.trim().length() == 0) {
            throw new UserStoreException(
                    "Required UserNameSearchFilter property is not set at the LDAP configurations");
        }

        String usernameAttribute =
                realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_ATTRIBUTE);
        if (usernameAttribute == null || usernameAttribute.trim().length() == 0) {
            throw new UserStoreException(
                    "Required UserNameAttribute property is not set at the LDAP configurations");
        }

        writeGroupsEnabled = false;

        // Groups properties
        if (realmConfig.getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED) != null) {
            readGroupsEnabled = Boolean.parseBoolean(realmConfig.
                    getUserStoreProperty(UserCoreConstants.RealmConfig.READ_GROUPS_ENABLED));
        }

        if (log.isDebugEnabled()) {
            String status = readGroupsEnabled ? "enabled" : "disabled";
            log.debug("ReadGroups is " + status);
        }

        if (readGroupsEnabled) {
            groupSearchBase = realmConfig.getUserStoreProperty(LDAPConstants.GROUP_SEARCH_BASE);
            if (groupSearchBase == null || groupSearchBase.trim().length() == 0) {
                throw new UserStoreException(
                        "Required GroupSearchBase property is not set at the LDAP configurations");
            }
            String groupNameListFilter =
                    realmConfig.getUserStoreProperty(LDAPConstants.GROUP_NAME_LIST_FILTER);
            if (groupNameListFilter == null || groupNameListFilter.trim().length() == 0) {
                throw new UserStoreException(
                        "Required GroupNameListFilter property is not set at the LDAP configurations");
            }

            String groupNameSearchFilter =
                    realmConfig.getUserStoreProperty(LDAPConstants.ROLE_NAME_FILTER);
            if (groupNameSearchFilter == null || groupNameSearchFilter.trim().length() == 0) {
                throw new UserStoreException(
                        "Required GroupNameSearchFilter property is not set at the LDAP configurations");
            }

            String groupNameAttribute =
                    realmConfig.getUserStoreProperty(LDAPConstants.GROUP_NAME_ATTRIBUTE);
            if (groupNameAttribute == null || groupNameAttribute.trim().length() == 0) {
                throw new UserStoreException(
                        "Required GroupNameAttribute property is not set at the LDAP configurations");
            }
            String memebershipAttribute =
                    realmConfig.getUserStoreProperty(LDAPConstants.MEMBERSHIP_ATTRIBUTE);
            if (memebershipAttribute == null || memebershipAttribute.trim().length() == 0) {
                throw new UserStoreException(
                        "Required MembershipAttribute property is not set at the LDAP configurations");
            }
        }

        // User DN cache properties
        cacheExpiryTimeAttribute = realmConfig.getUserStoreProperty(LDAPConstants.USER_CACHE_EXPIRY_MILLISECONDS);
        String userDnCacheEnabledAttribute = realmConfig.getUserStoreProperty(LDAPConstants.USER_DN_CACHE_ENABLED);
        if (StringUtils.isNotEmpty(userDnCacheEnabledAttribute)) {
            userDnCacheEnabled = Boolean.parseBoolean(userDnCacheEnabledAttribute);
        }
    }

    public boolean doAuthenticate(String userName, Object credential) throws UserStoreException {
        boolean debug = log.isDebugEnabled();

        String failedUserDN = null;

        if (userName == null || credential == null) {
            return false;
        }

        String leadingOrTrailingSpaceAllowedInUserName = realmConfig.getUserStoreProperty(UserCoreConstants
                .RealmConfig.LEADING_OR_TRAILING_SPACE_ALLOWED_IN_USERNAME);
        if (StringUtils.isNotEmpty(leadingOrTrailingSpaceAllowedInUserName)) {
            boolean isSpaceAllowedInUserName = Boolean.parseBoolean(leadingOrTrailingSpaceAllowedInUserName);
            if (log.isDebugEnabled()) {
                log.debug("'LeadingOrTrailingSpaceAllowedInUserName' property is set to : " +
                        isSpaceAllowedInUserName);
            }
            if (!isSpaceAllowedInUserName) {
                if (log.isDebugEnabled()) {
                    log.debug("Leading or trailing spaces are not allowed in username. Hence validating the username" +
                            " against the regex for the user : " + userName);
                }
            }
        } else {
            // Keeping old behavior for backward-compatibility.
            userName = userName.trim();
        }

        Secret credentialObj;
        try {
            credentialObj = Secret.getSecret(credential);
        } catch (UnsupportedSecretTypeException e) {
            throw new DashboardUserStoreException("Unsupported credential type", e);
        }

        if (userName.equals("") || credentialObj.isEmpty()) {
            return false;
        }

        if (debug) {
            log.debug("Authenticating user " + userName);
        }

        boolean bValue = false;
        String name = getNameInSpaceForUsernameFromLDAP(userName);
        try {
            if (name != null) {
                // if it is the same user DN found in the cache no need of futher authentication required.
                if (failedUserDN == null || !failedUserDN.equalsIgnoreCase(name)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authenticating with " + name);
                    }
                    bValue = this.bindAsUser(userName, name, credentialObj);
                }
            }
        } catch (NamingException | UserStoreException e) {
            String errorMessage = "Cannot bind user : " + userName;
            if (log.isDebugEnabled()) {
                log.debug(errorMessage, e);
            }
            throw new DashboardUserStoreException(errorMessage, e);
        }
        return bValue;

    }

    @Override
    protected String[] doListUsers(String filter, int maxItemLimit) throws UserStoreException {
        return new String[0];
    }

    @Override
    protected String[] doGetExternalRoleListOfUser(String userName, String filter) throws UserStoreException {
        // Get the effective search base
        String searchBase = this.getEffectiveSearchBase(false);
        return getLDAPRoleListOfUser(userName, filter, searchBase, false);
    }

    /**
     * This method will check whether back link support is enabled and will
     * return the effective
     * search base. Read http://www.frickelsoft.net/blog/?p=130 for more
     * details.
     *
     * @param shared whether share search based or not
     * @return The search base based on back link support. If back link support
     * is enabled this will
     * return user search base, else group search base.
     */
    protected String getEffectiveSearchBase(boolean shared) {

        String backLinksEnabled =
                realmConfig.getUserStoreProperty(LDAPConstants.BACK_LINKS_ENABLED);
        boolean isBackLinkEnabled = false;

        if (backLinksEnabled != null && !backLinksEnabled.equals("")) {
            isBackLinkEnabled = Boolean.parseBoolean(backLinksEnabled);
        }

        if (isBackLinkEnabled) {
            return realmConfig.getUserStoreProperty(LDAPConstants.USER_SEARCH_BASE);
        } else {
            if (shared) {
                return realmConfig.getUserStoreProperty(LDAPConstants.SHARED_GROUP_SEARCH_BASE);
            } else {
                return realmConfig.getUserStoreProperty(LDAPConstants.GROUP_SEARCH_BASE);
            }
        }

    }

    protected String[] getLDAPRoleListOfUser(String userName, String filter, String searchBase,
                                             boolean shared) throws UserStoreException {
        if (userName == null) {
            throw new DashboardUserStoreException("userName value is null.");
        }
        boolean debug = log.isDebugEnabled();
        List<String> list = new ArrayList<String>();
        if (readGroupsEnabled) {
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String memberOfProperty =
                    realmConfig.getUserStoreProperty(LDAPConstants.MEMBEROF_ATTRIBUTE);
            if (memberOfProperty != null && memberOfProperty.length() > 0) {
                String userNameProperty =
                        realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_ATTRIBUTE);
                String userSearchFilter = realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_SEARCH_FILTER);
                String searchFilter = userSearchFilter.replace("?", escapeSpecialCharactersForFilter(userName));

                String binaryAttribute =
                        realmConfig.getUserStoreProperty(LDAPConstants.LDAP_ATTRIBUTES_BINARY);
                String primaryGroupId =
                        realmConfig.getUserStoreProperty(LDAPConstants.PRIMARY_GROUP_ID);

                String returnedAtts[] = {memberOfProperty};

                if (binaryAttribute != null && primaryGroupId != null) {
                    returnedAtts =
                            new String[]{memberOfProperty, binaryAttribute, primaryGroupId};
                }

                searchCtls.setReturningAttributes(returnedAtts);

                if (debug) {
                    log.debug("Reading roles with the memberOfProperty Property: " + memberOfProperty);
                }

                if (binaryAttribute != null && primaryGroupId != null) {

                } else {
                    List<LdapName> groups = new ArrayList<>();
                }
            } else {

                // Load normal roles with the user
                String searchFilter;
                String roleNameProperty;

                if (shared) {
                    searchFilter = realmConfig.
                            getUserStoreProperty(LDAPConstants.SHARED_GROUP_NAME_LIST_FILTER);
                    roleNameProperty =
                            realmConfig.getUserStoreProperty(LDAPConstants.SHARED_GROUP_NAME_ATTRIBUTE);
                } else {
                    searchFilter = realmConfig.getUserStoreProperty(LDAPConstants.GROUP_NAME_LIST_FILTER);
                    roleNameProperty =
                            realmConfig.getUserStoreProperty(LDAPConstants.GROUP_NAME_ATTRIBUTE);
                }

                String membershipProperty =
                        realmConfig.getUserStoreProperty(LDAPConstants.MEMBERSHIP_ATTRIBUTE);
                String userDNPattern = realmConfig.getUserStoreProperty(LDAPConstants.USER_DN_PATTERN);
                String nameInSpace;
                if (userDNPattern != null && userDNPattern.trim().length() > 0 && !userDNPattern.contains("#")) {
                    nameInSpace = MessageFormat.format(userDNPattern, escapeSpecialCharactersForDN(userName));
                } else {
                    nameInSpace = this.getNameInSpaceForUserName(userName);
                }
                // read the roles with this membership property

                if (membershipProperty == null || membershipProperty.length() < 1) {
                    throw new DashboardUserStoreException(
                            "Please set member of attribute or membership attribute");
                }

                String membershipValue;
                if (nameInSpace != null) {
                    try {
                        LdapName ldn = new LdapName(nameInSpace);
                        if (MEMBER_UID.equals(realmConfig.getUserStoreProperty(LDAPConstants.MEMBERSHIP_ATTRIBUTE))) {
                            // membership value of posixGroup is not DN of the user
                            List rdns = ldn.getRdns();
                            membershipValue = ((Rdn) rdns.get(rdns.size() - 1)).getValue().toString();
                        } else {
                            membershipValue = escapeLdapNameForFilter(ldn);
                        }
                    } catch (InvalidNameException e) {
                        log.error("Error while creating LDAP name from: " + nameInSpace);
                        throw new DashboardUserStoreException("Invalid naming exception for : " + nameInSpace, e);
                    }
                } else {
                    return new String[0];
                }

                searchFilter =
                        "(&" + searchFilter + "(" + membershipProperty + "=" + membershipValue + "))";
                String returnedAtts[] = {roleNameProperty};
                searchCtls.setReturningAttributes(returnedAtts);

                if (debug) {
                    log.debug("Reading roles with the membershipProperty Property: " + membershipProperty);
                }

                list = this.getListOfNames(searchBase, searchFilter, searchCtls, roleNameProperty, false);
            }
        }

        String[] result = list.toArray(new String[list.size()]);

        if (result != null) {
            for (String rolename : result) {
                log.debug("Found role: " + rolename);
            }
        }
        return result;
    }

    /**
     * @param searchBases
     * @param searchFilter
     * @param searchCtls
     * @param property
     * @return
     * @throws UserStoreException
     */
    private List<String> getListOfNames(String searchBases, String searchFilter,
                                        SearchControls searchCtls, String property, boolean appendDn)
            throws UserStoreException {
        boolean debug = log.isDebugEnabled();
        List<String> names = new ArrayList<String>();
        DirContext dirContext = null;
        NamingEnumeration<SearchResult> answer = null;

        if (debug) {
            log.debug("Result for searchBase: " + searchBases + " searchFilter: " + searchFilter +
                    " property:" + property + " appendDN: " + appendDn);
        }

        try {
            dirContext = connectionSource.getContext();

            // handle multiple search bases
            String[] searchBaseArray = searchBases.split("#");
            for (String searchBase : searchBaseArray) {

                try {
                    answer = dirContext.search(escapeDNForSearch(searchBase), searchFilter, searchCtls);


                    while (answer.hasMoreElements()) {
                        SearchResult sr = answer.next();
                        if (sr.getAttributes() != null) {
                            Attribute attr = sr.getAttributes().get(property);
                            if (attr != null) {
                                for (Enumeration vals = attr.getAll(); vals.hasMoreElements(); ) {
                                    String name = (String) vals.nextElement();
                                    if (debug) {
                                        log.debug("Found user: " + name);
                                    }
                                    names.add(name);
                                }
                            }
                        }
                    }
                } catch (NamingException e) {
                    // ignore
                    if (log.isDebugEnabled()) {
                        log.debug(e);
                    }
                }

                if (debug) {
                    for (String name : names) {
                        log.debug("Result  :  " + name);
                    }
                }

            }

            return names;
        } finally {
            closeContextAndNamingEnumeration(dirContext, answer);
        }
    }

    /**
     * This method escapes the special characters in a LdapName
     * according to the ldap filter escaping standards
     *
     * @param ldn
     * @return
     */
    private String escapeLdapNameForFilter(LdapName ldn) {

        if (ldn == null) {
            if (log.isDebugEnabled()) {
                log.debug("Received null value to escape special characters. Returning null");
            }
            return null;
        }

        boolean replaceEscapeCharacters = true;

        String replaceEscapeCharactersAtUserLoginString = realmConfig
                .getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_REPLACE_ESCAPE_CHARACTERS_AT_USER_LOGIN);

        if (replaceEscapeCharactersAtUserLoginString != null) {
            replaceEscapeCharacters = Boolean
                    .parseBoolean(replaceEscapeCharactersAtUserLoginString);
            if (log.isDebugEnabled()) {
                log.debug("Replace escape characters configured to: "
                        + replaceEscapeCharactersAtUserLoginString);
            }
        }

        if (replaceEscapeCharacters) {
            String escapedDN = "";
            for (int i = ldn.size() - 1; i > -1; i--) { //escaping the rdns separately and re-constructing the DN
                escapedDN = escapedDN + escapeSpecialCharactersForFilterWithStarAsRegex(ldn.get(i));
                if (i != 0) {
                    escapedDN += ",";
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Escaped DN value for filter : " + escapedDN);
            }
            return escapedDN;
        } else {
            return ldn.toString();
        }
    }

    /**
     * Escaping ldap search filter special characters in a string
     *
     * @param dnPartial
     * @return
     */
    private String escapeSpecialCharactersForFilterWithStarAsRegex(String dnPartial) {
        boolean replaceEscapeCharacters = true;

        String replaceEscapeCharactersAtUserLoginString = realmConfig
                .getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_REPLACE_ESCAPE_CHARACTERS_AT_USER_LOGIN);

        if (replaceEscapeCharactersAtUserLoginString != null) {
            replaceEscapeCharacters = Boolean
                    .parseBoolean(replaceEscapeCharactersAtUserLoginString);
            if (log.isDebugEnabled()) {
                log.debug("Replace escape characters configured to: "
                        + replaceEscapeCharactersAtUserLoginString);
            }
        }

        if (replaceEscapeCharacters) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dnPartial.length(); i++) {
                char currentChar = dnPartial.charAt(i);
                switch (currentChar) {
                    case '\\':
                        if (dnPartial.charAt(i + 1) == '*') {
                            sb.append("\\2a");
                            i++;
                            break;
                        }
                        sb.append("\\5c");
                        break;
                    case '(':
                        sb.append("\\28");
                        break;
                    case ')':
                        sb.append("\\29");
                        break;
                    case '\u0000':
                        sb.append("\\00");
                        break;
                    default:
                        sb.append(currentChar);
                }
            }
            return sb.toString();
        } else {
            return dnPartial;
        }
    }

    /**
     * @param userName
     * @return
     * @throws UserStoreException
     */
    protected String getNameInSpaceForUserName(String userName) throws UserStoreException {

        // check the cache first
        LdapName ldn = null;
        if (userName != null) {
            ldn = getFromUserCache(userName);
        } else {
            throw new DashboardUserStoreException("userName value is null.");
        }
        if (ldn != null) {
            return ldn.toString();
        }

        return getNameInSpaceForUsernameFromLDAP(userName);
    }

    /**
     * Returns the LDAP Name (DN) for the given user name, if it exists in the cache.
     *
     * @param userName
     * @return cached DN, if exists. null if the cache does not contain the DN for the userName.
     */
    protected LdapName getFromUserCache(String userName) {
        try {
            Cache<String, LdapName> userDnCache = createOrGetUserDnCache();
            if (userDnCache == null) {
                // User cache may be null while initializing.
                return null;
            }
            return userDnCache.get(userName);
        } catch (IllegalStateException e) {
            log.error("Error occurred while getting User DN from cache having search base : " + userSearchBase, e);
            return null;
        }
    }

    /**
     * Returns the User DN Cache. Creates one if not exists in the cache manager.
     * Cache manager removes the cache if it is idle and empty for some time. Hence we need to create,
     * with our owen settings if needed.
     *
     * @return
     */
    private Cache<String, LdapName> createOrGetUserDnCache() {
        if (cacheManager == null || !userDnCacheEnabled) {
            if (log.isDebugEnabled()) {
                log.debug("Not using the cache on UserDN. cacheManager: " + cacheManager + " , Enabled : "
                        + userDnCacheEnabled);
            }
            return null;
        }

        Cache<String, LdapName> userDnCache;

        if (userDnCacheBuilder != null) {
            // We use cache builder to create the cache with custom expiry values.
            if (log.isDebugEnabled()) {
                log.debug("Using cache bulder to get the cache, for UserSearchBase: " + userSearchBase);
            }
            userDnCache = userDnCacheBuilder.build();
        } else {
            // We use system-wide settings to build the cache.
            if (log.isDebugEnabled()) {
                log.debug("Using default configurations for the user DN cache, having search base : " + userSearchBase);
            }
            userDnCache = cacheManager.getCache(userDnCacheName);
        }

        return userDnCache;
    }

    @Override
    protected String[] doGetUserListOfRole(String roleName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUser(String s, Object o, String[] roles, Map<String, String> map, String s1, boolean b) throws UserStoreException {

    }

    @Override
    protected boolean doCheckExistingUser(String userName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doAddUser(String userName, Object credential, String[] roleList, Map<String, String> claims, String profileName, boolean requirePasswordChange) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDeleteUser(String userName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdateRoleListOfUser(String userName, String[] deletedRoles, String[] newRoles) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDeleteRole(String roleName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdateCredentialByAdmin(String userName, Object newCredential) throws UserStoreException {

    }

    @Override
    protected void doUpdateCredential(String userName, Object newCredential, Object oldCredential) throws UserStoreException {
        throw new UnsupportedOperationException();

    }

    @Override
    protected String[] doGetRoleNames(String filter, int maxItemLimit) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doCheckExistingRole(String roleName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected RoleContext createRoleContext(String roleName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRole(String s, String[] strings, Permission[] permissions, boolean b) throws UserStoreException {

    }

    @Override
    protected void doAddRole(String roleName, String[] userList) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doCheckIsUserInRole(String userName, String roleName) throws UserStoreException {
        throw new UnsupportedOperationException();
    }

    /**
     * This is to search user and retrieve ldap name directly from ldap
     *
     * @param userName
     * @return
     * @throws DashboardUserStoreException
     */
    protected String getNameInSpaceForUsernameFromLDAP(String userName) throws UserStoreException {

        String searchBase = null;
        String userSearchFilter = realmConfig.getUserStoreProperty(LDAPConstants.USER_NAME_SEARCH_FILTER);
        userSearchFilter = userSearchFilter.replace("?", escapeSpecialCharactersForFilter(userName));
        String userDNPattern = realmConfig.getUserStoreProperty(LDAPConstants.USER_DN_PATTERN);
        if (userDNPattern != null && userDNPattern.trim().length() > 0) {
            String[] patterns = userDNPattern.split("#");
            for (String pattern : patterns) {
                searchBase = MessageFormat.format(pattern, escapeSpecialCharactersForDN(userName));
                String userDN = null;
                try {
                    userDN = getNameInSpaceForUserName(userName, searchBase, userSearchFilter);
                } catch (UserStoreException e) {
                    throw new DashboardUserStoreException(e.getMessage(), e);
                }
                // check in another DN pattern
                if (userDN != null) {
                    return userDN;
                }
            }
        }

        searchBase = realmConfig.getUserStoreProperty(LDAPConstants.USER_SEARCH_BASE);
        try {
            return getNameInSpaceForUserName(userName, searchBase, userSearchFilter);
        } catch (UserStoreException e) {
            throw new DashboardUserStoreException(e.getMessage(), e);
        }
    }

    /**
     * Escaping ldap search filter special characters in a string
     *
     * @param dnPartial String to replace special characters of
     * @return
     */
    private String escapeSpecialCharactersForFilter(String dnPartial) {
        boolean replaceEscapeCharacters = true;
        dnPartial.replace("\\*", "*");

        String replaceEscapeCharactersAtUserLoginString = realmConfig
                .getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_REPLACE_ESCAPE_CHARACTERS_AT_USER_LOGIN);

        if (replaceEscapeCharactersAtUserLoginString != null) {
            replaceEscapeCharacters = Boolean
                    .parseBoolean(replaceEscapeCharactersAtUserLoginString);
            if (log.isDebugEnabled()) {
                log.debug("Replace escape characters configured to: "
                        + replaceEscapeCharactersAtUserLoginString);
            }
        }

        if (replaceEscapeCharacters) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dnPartial.length(); i++) {
                char currentChar = dnPartial.charAt(i);
                switch (currentChar) {
                    case '\\':
                        sb.append("\\5c");
                        break;
                    case '*':
                        sb.append("\\2a");
                        break;
                    case '(':
                        sb.append("\\28");
                        break;
                    case ')':
                        sb.append("\\29");
                        break;
                    case '\u0000':
                        sb.append("\\00");
                        break;
                    default:
                        sb.append(currentChar);
                }
            }
            return sb.toString();
        } else {
            return dnPartial;
        }
    }

    /**
     * Escaping ldap DN special characters in a String value
     *
     * @param text String to replace special characters of
     * @return
     */
    private String escapeSpecialCharactersForDN(String text) {
        boolean replaceEscapeCharacters = true;
        text.replace("\\*", "*");

        String replaceEscapeCharactersAtUserLoginString = realmConfig
                .getUserStoreProperty(UserCoreConstants.RealmConfig.PROPERTY_REPLACE_ESCAPE_CHARACTERS_AT_USER_LOGIN);

        if (replaceEscapeCharactersAtUserLoginString != null) {
            replaceEscapeCharacters = Boolean
                    .parseBoolean(replaceEscapeCharactersAtUserLoginString);
            if (log.isDebugEnabled()) {
                log.debug("Replace escape characters configured to: "
                        + replaceEscapeCharactersAtUserLoginString);
            }
        }

        if (replaceEscapeCharacters) {
            StringBuilder sb = new StringBuilder();
            if ((text.length() > 0) && ((text.charAt(0) == ' ') || (text.charAt(0) == '#'))) {
                sb.append('\\'); // add the leading backslash if needed
            }
            for (int i = 0; i < text.length(); i++) {
                char currentChar = text.charAt(i);
                switch (currentChar) {
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case ',':
                        sb.append("\\,");
                        break;
                    case '+':
                        sb.append("\\+");
                        break;
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '<':
                        sb.append("\\<");
                        break;
                    case '>':
                        sb.append("\\>");
                        break;
                    case ';':
                        sb.append("\\;");
                        break;
                    case '*':
                        sb.append("\\2a");
                        break;
                    default:
                        sb.append(currentChar);
                }
            }
            if ((text.length() > 1) && (text.charAt(text.length() - 1) == ' ')) {
                sb.insert(sb.length() - 1, '\\'); // add the trailing backslash if needed
            }
            if (log.isDebugEnabled()) {
                log.debug("value after escaping special characters in " + text + " : " + sb.toString());
            }
            return sb.toString();
        } else {
            return text;
        }
    }

    /**
     * @param userName
     * @param searchBase
     * @param searchFilter
     * @return
     * @throws UserStoreException
     */
    protected String getNameInSpaceForUserName(String userName, String searchBase, String searchFilter)
            throws UserStoreException {
        boolean debug = log.isDebugEnabled();

        if (userName == null) {
            throw new DashboardUserStoreException("userName value is null.");
        }

        String userDN = null;

        DirContext dirContext = this.connectionSource.getContext();
        NamingEnumeration<SearchResult> answer = null;
        try {
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            if (log.isDebugEnabled()) {
                try {
                    log.debug("Searching for user with SearchFilter: " + searchFilter + " in SearchBase: " + dirContext.getNameInNamespace());
                } catch (NamingException e) {
                    log.debug("Error while getting DN of search base", e);
                }
            }
            SearchResult userObj = null;
            String[] searchBases = searchBase.split("#");
            for (String base : searchBases) {
                answer = dirContext.search(escapeDNForSearch(base), searchFilter, searchCtls);
                if (answer.hasMore()) {
                    userObj = (SearchResult) answer.next();
                    if (userObj != null) {
                        //no need to decode since , if decoded the whole string, can't be encoded again
                        //eg CN=Hello\,Ok=test\,test, OU=Industry
                        userDN = userObj.getNameInNamespace();
                        break;
                    }
                }
            }
            if (debug) {
                log.debug("Name in space for " + userName + " is " + userDN);
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        } finally {
            closeContextAndNamingEnumeration(dirContext, answer);
        }
        return userDN;
    }

    private static void closeContextAndNamingEnumeration(DirContext dirContext, NamingEnumeration<SearchResult> answer)
            throws UserStoreException {
        JNDIUtil.closeNamingEnumeration(answer);

        JNDIUtil.closeContext(dirContext);

    }

    /**
     * This method performs the additional level escaping for ldap search. In ldap search / and " characters
     * have to be escaped again
     *
     * @param dn DN
     * @return composite name
     * @throws InvalidNameException failed to build composite name
     */
    protected Name escapeDNForSearch(String dn) throws InvalidNameException {
        // This is done to escape '/' which is not a LDAP special character but a JNDI special character.
        // Refer: https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4307193
        return new CompositeName().add(dn);
    }

    /**
     * @param userName
     * @param dn
     * @param credentials
     * @return
     * @throws NamingException
     * @throws UserStoreException
     */
    private boolean bindAsUser(String userName, String dn, Object credentials) throws NamingException,
            UserStoreException {
        boolean isAuthed = false;
        boolean debug = log.isDebugEnabled();

        /*
         * Hashtable<String, String> env = new Hashtable<String, String>();
         * env.put(Context.INITIAL_CONTEXT_FACTORY, LDAPConstants.DRIVER_NAME);
         * env.put(Context.SECURITY_PRINCIPAL, dn);
         * env.put(Context.SECURITY_CREDENTIALS, credentials);
         * env.put("com.sun.jndi.ldap.connect.pool", "true");
         */
        /**
         * In carbon JNDI context we need to by pass specific tenant context and
         * we need the base
         * context for LDAP operations.
         */
        // env.put(CarbonConstants.REQUEST_BASE_CONTEXT, "true");

        /*
         * String rawConnectionURL =
         * realmConfig.getUserStoreProperty(LDAPConstants.CONNECTION_URL);
         * String portInfo = rawConnectionURL.split(":")[2];
         *
         * String connectionURL = null;
         * String port = null;
         * // if the port contains a template string that refers to carbon.xml
         * if ((portInfo.contains("${")) && (portInfo.contains("}"))) {
         * port =
         * Integer.toString(CarbonUtils.getPortFromServerConfig(portInfo));
         * connectionURL = rawConnectionURL.replace(portInfo, port);
         * }
         * if (port == null) { // if not enabled, read LDAP url from
         * user.mgt.xml
         * connectionURL =
         * realmConfig.getUserStoreProperty(LDAPConstants.CONNECTION_URL);
         * }
         */
        /*
         * env.put(Context.PROVIDER_URL, connectionURL);
         * env.put(Context.SECURITY_AUTHENTICATION, "simple");
         */

        LdapContext cxt = null;
        try {
            // cxt = new InitialLdapContext(env, null);
            cxt = this.connectionSource.getContextWithCredentials(dn, credentials);
            isAuthed = true;
        } catch (AuthenticationException e) {
            /*
             * StringBuilder stringBuilder = new
             * StringBuilder("Authentication failed for user ");
             * stringBuilder.append(dn).append(" ").append(e.getMessage());
             */

            // we avoid throwing an exception here since we throw that exception
            // in a one level above this.
            if (debug) {
                log.debug("Authentication failed " + e);
                log.debug("Clearing cache for DN: " + dn);
            }
        } finally {
            JNDIUtil.closeContext(cxt);
        }

        if (debug) {
            log.debug("User: " + dn + " is authenticated: " + isAuthed);
        }
        return isAuthed;
    }

    @Override
    public boolean isExistingUser(String s) throws UserStoreException {
        return false;
    }

    @Override
    public String[] getProfileNames(String s) throws UserStoreException {
        return new String[0];
    }

    @Override
    public String getUserClaimValue(String s, String s1, String s2) throws UserStoreException {
        return "";
    }

    @Override
    public Map<String, String> getUserClaimValues(String s, String[] strings, String s1) throws UserStoreException {
        return Map.of();
    }

    @Override
    public Claim[] getUserClaimValues(String s, String s1) throws UserStoreException {
        return new Claim[0];
    }

    @Override
    public String[] getAllProfileNames() throws UserStoreException {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void addUser(String s, Object o, String[] strings, Map<String, String> map, String s1) throws UserStoreException {

    }

    @Override
    public void updateUserListOfRole(String s, String[] strings, String[] strings1) throws UserStoreException {

    }

    @Override
    public void setUserClaimValue(String s, String s1, String s2, String s3) throws UserStoreException {

    }

    @Override
    public void setUserClaimValues(String s, Map<String, String> map, String s1) throws UserStoreException {

    }

    @Override
    public void deleteUserClaimValue(String s, String s1, String s2) throws UserStoreException {

    }

    @Override
    public void deleteUserClaimValues(String s, String[] strings, String s1) throws UserStoreException {

    }

    @Override
    public String[] getAllSecondaryRoles() throws UserStoreException {
        return new String[0];
    }

    @Override
    public Date getPasswordExpirationTime(String s) throws UserStoreException {
        return null;
    }

    @Override
    public int getUserId(String s) throws UserStoreException {
        return 0;
    }

    @Override
    public int getTenantId(String s) throws UserStoreException {
        return 0;
    }

    /**
     *
     */
    public int getTenantId() {
        return this.tenantId;
    }

    @Override
    public Map<String, String> getProperties(Tenant tenant) throws UserStoreException {
        return Map.of();
    }

    @Override
    public void updateRoleName(String s, String s1) throws UserStoreException {

    }

    @Override
    public boolean isBulkImportSupported() throws UserStoreException {
        return false;
    }

    @Override
    public String[] getUserList(String s, String s1, String s2) throws UserStoreException {
        return new String[0];
    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager() {
        return null;
    }

    @Override
    public void setSecondaryUserStoreManager(UserStoreManager userStoreManager) {

    }

    @Override
    public UserStoreManager getSecondaryUserStoreManager(String s) {
        return null;
    }

    @Override
    public void addSecondaryUserStoreManager(String s, UserStoreManager userStoreManager) {

    }

    /**
     *
     */
    public RealmConfiguration getRealmConfiguration() {
        return this.realmConfig;
    }

    @Override
    public boolean isExistingRole(String s, boolean b) throws org.wso2.micro.integrator.security.user.api.UserStoreException {
        return false;
    }

    @Override
    public void addRole(String s, String[] strings, Permission[] permissions) throws org.wso2.micro.integrator.security.user.api.UserStoreException {

    }

    @Override
    public Map<String, String> getProperties(org.wso2.micro.integrator.security.user.api.Tenant tenant) throws org.wso2.micro.integrator.security.user.api.UserStoreException {
        return Map.of();
    }

    @Override
    public boolean isMultipleProfilesAllowed() {
        return false;
    }

    @Override
    public void addRememberMe(String s, String s1) throws org.wso2.micro.integrator.security.user.api.UserStoreException {

    }

    @Override
    public boolean isValidRememberMeToken(String s, String s1) throws org.wso2.micro.integrator.security.user.api.UserStoreException {
        return false;
    }

    @Override
    public org.wso2.micro.integrator.security.user.api.ClaimManager getClaimManager() throws org.wso2.micro.integrator.security.user.api.UserStoreException {
        return null;
    }

    @Override
    public boolean isSCIMEnabled() throws org.wso2.micro.integrator.security.user.api.UserStoreException {
        return false;
    }

    @Override
    public Properties getDefaultUserStoreProperties() {
        return null;
    }
}
