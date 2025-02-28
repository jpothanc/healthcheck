package com.ibit.services;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Get Secret Key from Environment Variable
    private static SecretKeySpec getSecretKey(String key) {
        if (key == null || key.length() != 16) {
            throw new RuntimeException("ENCRYPTION_KEY must be exactly 16 characters long.");
        }
        System.out.println("Key: " + key);
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    // Encrypt Method
    public static String encrypt(String plaintext,String key) {
        try {
            SecretKeySpec secretKey = getSecretKey(key);
            System.out.println("Secret Key: " + secretKey);

            // Generate a random IV
            byte[] iv = new byte[16];
            java.security.SecureRandom.getInstanceStrong().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV + encrypted data and encode in Base64
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // Decrypt Method
    public static String decrypt(String encryptedText, String key) {
        try {
            SecretKeySpec secretKey = getSecretKey(key);

            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            // Extract IV and encrypted data
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[decoded.length - 16];
            System.arraycopy(decoded, 0, iv, 0, 16);
            System.arraycopy(decoded, 16, encrypted, 0, encrypted.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public static String generateAESKey() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 16);
    }
}
