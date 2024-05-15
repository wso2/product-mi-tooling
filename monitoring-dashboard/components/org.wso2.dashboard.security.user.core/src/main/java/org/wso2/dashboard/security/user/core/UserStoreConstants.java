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

package org.wso2.dashboard.security.user.core;

public class UserStoreConstants {
    public static final String DEFAULT_LDAP_USERSTORE_MANAGER =
            "org.wso2.dashboard.security.user.core.ldap.ReadOnlyLDAPUserStoreManager";
    public static final String DEFAULT_JDBC_USERSTORE_MANAGER =
            "org.wso2.dashboard.security.user.core.jdbc.JDBCUserStoreManager";
    public static final String REGISTRY_SYSTEM_USERNAME = "wso2.system.user";
    public static String DOMAIN_SEPARATOR = "/";
    public static final String PRIMARY_DEFAULT_DOMAIN_NAME = "PRIMARY";
    public static final int SUPER_TENANT_ID = -1234;
    public static final String UM_TENANT_COLUMN = "UM_TENANT_ID";
    public static final String DATA_SOURCE = "um.datasource";

    public static final class RealmConfig {
        public static final String LOCAL_NAME_USER_MANAGER = "UserManager";
        public static final String LOCAL_NAME_REALM = "Realm";
        public static final String LOCAL_NAME_CONFIGURATION = "Configuration";
        public static final String LOCAL_NAME_PROPERTY = "Property";
        public static final String LOCAL_NAME_ADD_ADMIN = "AddAdmin";
        public static final String LOCAL_NAME_RESERVED_ROLE_NAMES = "ReservedRoleNames";
        public static final String LOCAL_NAME_RESTRICTED_DOMAINS_FOR_SELF_SIGN_UP = "RestrictedDomainsForSelfSignUp";
        public static final String LOCAL_NAME_ADMIN_ROLE = "AdminRole";
        public static final String LOCAL_NAME_ADMIN_USER = "AdminUser";
        public static final String LOCAL_NAME_USER_NAME = "UserName";
        public static final String LOCAL_NAME_PASSWORD = "Password";
        public static final String LOCAL_NAME_AUTHENTICATOR = "Authenticator";
        public static final String LOCAL_NAME_USER_STORE_MANAGER = "UserStoreManager";
        public static final String LOCAL_NAME_ATHZ_MANAGER = "AuthorizationManager";
        public static final String LOCAL_NAME_SYSTEM_USER_NAME = "SystemUserName";
        public static final String LOCAL_NAME_EVERYONE_ROLE = "EveryOneRoleName";
        public static final String LOCAL_NAME_ANONYMOUS_USER = "AnonymousUser";
        public static final String LOCAL_PASSWORDS_EXTERNALLY_MANAGED = "PasswordsExternallyManaged";
        public static final String OVERRIDE_USERNAME_CLAIM_FROM_INTERNAL_USERNAME = "OverrideUsernameClaimFromInternalUsername";
        public static final String ATTR_NAME_CLASS = "class";
        public static final String ATTR_NAME_PROP_NAME = "name";
        public static final String PROPERTY_EVERYONEROLE_AUTHORIZATION = "EveryoneRoleManagementPermissions";
        public static final String PROPERTY_ADMINROLE_AUTHORIZATION = "AdminRoleManagementPermissions";
        public static final String PROPERTY_UPDATE_PERM_TREE_PERIODICALLY = "UpdatePermissionTreePeriodically";
        public static final String PROPERTY_USERNAME_UNIQUE = "UserNameUniqueAcrossTenants";
        public static final String PROPERTY_IS_EMAIL_USERNAME = "IsEmailUserName";
        public static final String PROPERTY_DOMAIN_CALCULATION = "DomainCalculation";
        public static final String PROPERTY_IS_USERS_OF_ROLE_LISTING = "IsUsersOfRoleListing";
        public static final String PROPERTY_READ_ONLY = "ReadOnly";
        public static final String CLASS_DESCRIPTION = "Description";
        public static final String PROPERTY_PRESERVE_CASE_FOR_RESOURCES = "PreserveCaseForResources";
        public static final String EMAIL_VALIDATION_REGEX = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        /** @deprecated */
        @Deprecated
        public static final String PROPERTY_INTERNAL_ROLES_ONLY = "InternalJDBCRolesOnly";
        public static final String PROPERTY_MAX_USER_LIST = "MaxUserNameListLength";
        public static final String PROPERTY_MAX_ROLE_LIST = "MaxRoleNameListLength";
        public static final String PROPERTY_MAX_SEARCH_TIME = "MaxSearchQueryTime";
        public static final String READ_GROUPS_ENABLED = "ReadGroups";
        public static final String WRITE_GROUPS_ENABLED = "WriteGroups";
        public static final String USER_STORE_DISABLED = "Disabled";
        public static final String PROPERTY_VALUE_DOMAIN_CALCULATION_DEFAULT = "default";
        public static final String PROPERTY_VALUE_DOMAIN_CALCULATION_CUSTOM = "custom";
        public static final String PROPERTY_VALUE_DEFAULT_MAX_COUNT = "100";
        public static final String PROPERTY_VALUE_DEFAULT_READ_ONLY = "false";
        public static final String PROPERTY_JAVA_REG_EX = "PasswordJavaRegEx";
        public static final String PROPERTY_JS_REG_EX = "PasswordJavaScriptRegEx";
        public static final String PROPERTY_USER_NAME_JAVA_REG_EX = "UsernameJavaRegEx";
        public static final String PROPERTY_USER_NAME_JAVA_REG = "UserNameJavaRegEx";
        public static final String PROPERTY_USER_NAME_JS_REG_EX = "UsernameJavaScriptRegEx";
        public static final String PROPERTY_USER_NAME_JS_REG = "UserNameJavaScriptRegEx";
        public static final String PROPERTY_USER_NAME_WITH_EMAIL_JS_REG_EX = "UsernameWithEmailJavaScriptRegEx";
        public static final String PROPERTY_ROLE_NAME_JAVA_REG_EX = "RolenameJavaRegEx";
        public static final String PROPERTY_ROLE_NAME_JS_REG_EX = "RolenameJavaScriptRegEx";
        public static final String PROPERTY_EXTERNAL_IDP = "ExternalIdP";
        public static final String PROPERTY_KDC_ENABLED = "kdcEnabled";
        public static final String DEFAULT_REALM_NAME = "defaultRealmName";
        public static final String PROPERTY_SCIM_ENABLED = "SCIMEnabled";
        public static final String PROPERTY_ROLES_CACHE_ENABLED = "UserRolesCacheEnabled";
        public static final String PROPERTY_AUTHORIZATION_CACHE_ENABLED = "AuthorizationCacheEnabled";
        public static final String PROPERTY_CASE_SENSITIVITY = "CaseSensitiveAuthorizationRules";
        public static final String PROPERTY_USER_CORE_CACHE_IDENTIFIER = "UserCoreCacheIdentifier";
        public static final String PROPERTY_USER_ROLE_CACHE_TIME_OUT = "UserCoreCacheTimeOut";
        public static final String PROPERTY_REPLACE_ESCAPE_CHARACTERS_AT_USER_LOGIN = "ReplaceEscapeCharactersAtUserLogin";
        public static final String PASSWORD_HASH_METHOD_PLAIN_TEXT = "PLAIN_TEXT";
        public static final String PROPERTY_DOMAIN_NAME = "DomainName";
        public static final String STATIC_USER_STORE = "StaticUserStore";
        public static final String PROPERTY_GROUP_SEARCH_DOMAINS = "GroupSearchDomains";
        public static final String LOCAL_NAME_MULTIPLE_CREDENTIALS = "MultipleCredentials";
        public static final String LOCAL_NAME_CREDENTIAL = "Credential";
        public static final String ATTR_NAME_TYPE = "type";
        public static final String SHARED_GROUPS_ENABLED = "SharedGroupEnabled";
        public static final String DOMAIN_NAME_XPATH = "//UserStoreManager/Property[@name='DomainName']";
        public static final String LEADING_OR_TRAILING_SPACE_ALLOWED_IN_USERNAME = "LeadingOrTrailingSpaceAllowedInUserName";

        public RealmConfig() {
        }
    }
}
