/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.wso2.ei.dashboard.core.commons.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JwtUtil {
    private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    public static String generateToken(String username, String scope) {
        try {
            // Header JSON
            JsonObject jsonHeader = new JsonObject();
            jsonHeader.addProperty("alg", "HS256");
            jsonHeader.addProperty("typ", "JWT");
            String base64UrlHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonHeader.toString().getBytes());

            // Payload JSON with dynamic expiration
            JsonObject jsonPayload = new JsonObject();
            jsonPayload.addProperty("iss", "wso2.dashboard.com");
            jsonPayload.addProperty("sub", username);

            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + 3600 * 1000; // Adds one hour to the current time
            Date exp = new Date(expMillis);

            jsonPayload.addProperty("exp", exp.getTime() / 1000);
            jsonPayload.addProperty("scope", scope);
            String base64UrlPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonPayload.toString().getBytes());

            // Signature
            String secret = generateSecret();
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String dataToSign = base64UrlHeader + "." + base64UrlPayload;
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(dataToSign.getBytes()));

            // JWT
            return base64UrlHeader + "." + base64UrlPayload + "." + signature;
        } catch (Exception e) {
            logger.error("Error generating JWT", e);
            return null;
        }
    }

    private static String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return new String(bytes);
    }

    public static String extractSubject(String token) {
        try {
            // Split the token into header, payload, and signature
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format.");
            }
            // Decode the payload part (2nd part)
            String base64UrlPayload = tokenParts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(base64UrlPayload);
            String payloadJson = new String(decodedBytes);

            // Parse the payload JSON
            JsonObject payloadObject = JsonParser.parseString(payloadJson).getAsJsonObject();

            // Extract and return the "sub" field
            return payloadObject.has("sub") ? payloadObject.get("sub").getAsString() : null;
        } catch (Exception e) {
            logger.debug("Unable to extract subject from JWT", e);
            return null;
        }
    }

    public static boolean isJWTToken(String token) {
        String[] parts = token.split("\\.");
        return parts.length >= 2;
    }
}
