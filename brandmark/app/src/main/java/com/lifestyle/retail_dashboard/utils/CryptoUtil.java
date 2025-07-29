package com.lifestyle.retail_dashboard.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class CryptoUtil {

    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private static KeySpec keySpec;
    private static SecretKeyFactory secretKeyFactory;
    private static Cipher cipher;
    private static byte[] keyAsBytes;
    private  static String encryptionKey;
    private static  String encryptionScheme;
    private static SecretKey secretKey;
    private static boolean isInitialized = false;


    static{

        if (!isInitialized) {
            encryptionKey = "ThisIsSecretEncryptionKey";
            encryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            try {
                keyAsBytes = encryptionKey.getBytes(UNICODE_FORMAT);
                keySpec = new DESedeKeySpec(keyAsBytes);
                secretKeyFactory = SecretKeyFactory
                        .getInstance(encryptionScheme);
                cipher = Cipher.getInstance(encryptionScheme);
                secretKey = secretKeyFactory.generateSecret(keySpec);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isInitialized = true;
        }

    }

    /**
     * Method To Encrypt The String
     */
    public static String encrypt(String plainText) throws Exception {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] plainTextBytes = plainText.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainTextBytes);
            encryptedString = bytes2String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return encryptedString;
    }


    public static String decode(String plainText) throws Exception {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] plainTextBytes = plainText.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainTextBytes);
            encryptedString = bytes2String(Base64.decodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return encryptedString;
    }


    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }


}