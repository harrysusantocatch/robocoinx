package com.example.robocoinx.logic;

import android.util.Base64;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
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
            shaHex = new String(org.apache.commons.codec.binary.Hex.encodeHex(digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return shaHex.toLowerCase();
    }

    public static String toBaseEncode(String text){
        byte[] dataVal = text.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(dataVal, Base64.DEFAULT);
        return base64;
    }

    public static String toBaseDecode(String base64){
        byte[] dataVal = Base64.decode(base64, Base64.DEFAULT);
        String text = new String(dataVal, StandardCharsets.UTF_8);
        return text;
    }
}
