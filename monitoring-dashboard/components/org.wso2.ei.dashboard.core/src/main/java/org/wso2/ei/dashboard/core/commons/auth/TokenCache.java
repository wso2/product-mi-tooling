/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.ei.dashboard.core.commons.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import static org.wso2.ei.dashboard.core.commons.Constants.TOKEN_CACHE_TIMEOUT;

/**
 * Token Cache Implementation. Tokens will be invalidate after a interval of TOKEN_CACHE_TIMEOUT minutes.
 */
public class TokenCache {

    private static final TokenCache instance = new TokenCache();

    private final Cache<String, String> tokenMap =
            CacheBuilder.newBuilder().expireAfterWrite(TOKEN_CACHE_TIMEOUT, TimeUnit.MINUTES).build();

    private TokenCache() {

    }

    /**
     * Get TokenCache Instance.
     *
     * @return TokenCache
     */
    public static TokenCache getInstance() {
        return instance;
    }

    /**
     * This method store the token in the cache.
     *
     * @param key       JWT access token identifier
     * @param token     JWT access token
     */
    public void putToken(String key, String token) {
        tokenMap.put(key, token);
    }

    /**
     * This method returns the value in the cache.
     *
     * @param id       JWT access token identifier
     * @return Token string
     */
    public String getToken(String id) {
        return tokenMap.getIfPresent(id);
    }

    /**
     * This method is called to remove the token from the cache.
     *
     * @param id JWT access token identifier
     */
    public void removeToken(String id) {
        tokenMap.invalidate(id);
    }
}
