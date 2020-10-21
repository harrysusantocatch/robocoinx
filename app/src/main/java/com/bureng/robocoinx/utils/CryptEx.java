package com.bureng.robocoinx.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptEx {

    public static String getSha256Hex(String text){
        String shaHex = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();

//            shaHex = DatatypeConverter.printHexBinary(digest);
            // TODO check
            shaHex = new String(org.apache.commons.codec.binary.Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return shaHex.toLowerCase();
    }

    public static String toBaseEncode(String text) {
        byte[] dataVal = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(dataVal, Base64.DEFAULT);
    }

    public static String toBaseDecode(String base64) {
        byte[] dataVal = Base64.decode(base64, Base64.DEFAULT);
        return new String(dataVal, StandardCharsets.UTF_8);
    }

    public static String encryptAES(String key, String text) {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : encrypted) {
                sb.append((char) b);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decryptAES(String key, String text) {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            byte[] bb = new byte[text.length()];
            for (int i = 0; i < text.length(); i++) {
                bb[i] = (byte) text.charAt(i);
            }
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(bb));
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
