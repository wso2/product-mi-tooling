package org.wso2.ei.dashboard.core.commons.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import com.google.gson.JsonObject;

public class TokenGenerator {

    public static String generateToken(String username, String scope) {
        String jwt = null;
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
            jwt = base64UrlHeader + "." + base64UrlPayload + "." + signature;
        } catch (Exception e) {
            System.err.println("Error in generating JWT: " + e.getMessage());
        }
        return jwt;
    }

    private static String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);

        return new String(bytes);
    }
}
