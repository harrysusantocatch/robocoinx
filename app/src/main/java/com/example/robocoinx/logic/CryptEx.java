package com.example.robocoinx.logic;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;

public class CryptEx {

    public static String getSha256Hex(String text){
        String shaHex = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(text.getBytes("UTF-8"));
            byte[] digest = md.digest();

//            shaHex = DatatypeConverter.printHexBinary(digest);
            // TODO check
            shaHex = org.apache.commons.codec.binary.Hex.encodeHex(digest).toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return shaHex.toLowerCase();
    }

    public static String toBaseEncode(String text, String key){
        String encoded = null;
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            encoded = new String(encrypted);
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static String toBaseDecode(String text, String key){
        String decoded = null;
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decoded = new String(cipher.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return decoded;
    }
}
