package com.bigdata.postgres;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;



public  class Aes {

    /**
     *
     * @param algorithm encryption algorithm
     * @param plainText plaintext
     * @param key private key
     * @return Base64 Encoded cipherText
     */


    public static String encrypt( String plainText,  String key, String algorithm)
    {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();

        }
        try {

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     *
     * @param algorithm encryption algorithm
     * @param cipherText ciphertext
     * @param key private key
     */

    public  static  String decrypt(String cipherText, String key, String algorithm)
    {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] plainText = new byte[0];
        try {
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(plainText);
    }

}
