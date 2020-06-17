package io.incubator.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Noa Swartz
 * @date 2020-04-12
 */
public class Hex {

    public static final String MD5 = "MD5";
    public static final String SHA_256 = "SHA-256";
    private static final String HEX_STR =  "0123456789ABCDEF";

    /**
     * byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytes2hex(byte[] bytes) {
        if(null == bytes){
            return null;
        }
        StringBuilder result = new StringBuilder();
        String hex;
        for (byte aByte : bytes) {
            // height 4 bit
            hex = String.valueOf(HEX_STR.charAt((aByte & 0xF0) >> 4));
            // low 4 bit
            hex += String.valueOf(HEX_STR.charAt(aByte & 0x0F));
            result.append(hex);
        }
        return result.toString();
    }

    /**
     * hex string to byte array
     *
     * @param hexString
     * @return
     */
    public static byte[] hex2bytes(String hexString) {
        if(hexString == null ){
            return null;
        }
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        byte high, low;
        for (int i = 0; i < len; i++) {
            high = (byte) ((HEX_STR.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) HEX_STR.indexOf(hexString.charAt(2 * i + 1));
            bytes[i] = (byte) (high | low);
        }
        return bytes;
    }

    public static String md5Hex(String content) {
        try {
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            md5.update(data);
            byte[] result = md5.digest();
            return bytes2hex(result);
        } catch (NoSuchAlgorithmException e0) {
            e0.printStackTrace();
        }
        return null;
    }

    public static String sha256Hex(String content) {
        return sha256Hex(content.getBytes());
    }

    /**
     * file sha256
     *
     * @param path
     * @return
     */
    public static String sha256Hex(Path path) {
        try {
            return sha256Hex(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);
            messageDigest.update(bytes);
            return bytes2hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
