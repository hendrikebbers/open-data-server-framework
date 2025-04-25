package com.openelements.data.server.internal;

import java.security.MessageDigest;
import org.jspecify.annotations.NonNull;

public class HashUtil {

    @NonNull
    public static byte[] calculateHash(final @NonNull byte[] content) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA-256");
            return instance.digest(content);
        } catch (Exception e) {
            throw new RuntimeException("Error in calculating hash", e);
        }
    }

    @NonNull
    public static String bytesToHex(final @NonNull byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
