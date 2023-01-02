package com.bigdata.postgres.utils;

import com.bigdata.postgres.Model.Response;
import com.bigdata.postgres.Model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Helper {
    static String algorithm = "AES/CBC/PKCS5Padding";
    public final Long cachedItemsExpiry = 1L;

    public static String encrypt( String plainText,  String key)
    {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return  "Invalid";
        }

        try {

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return  "Invalid";
        }
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return  "Invalid";
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     *
     * @param cipherText ciphertext
     * @param key private key
     */

    public  static  String decrypt(String cipherText, String key)
    {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            return  "Invalid";
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {

            e.printStackTrace();
            return  "Invalid";
        }

        byte[] plainText;
        try {
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return  "Invalid";
        }catch (IllegalBlockSizeException e){
           return cipherText;
        }
        return new String(plainText);
    }
}
