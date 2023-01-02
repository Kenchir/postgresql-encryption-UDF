package com.bigdata.postgres;


import com.bigdata.postgres.Model.Response;

import com.bigdata.postgres.Model.Token;
import com.bigdata.postgres.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class Aes {

    static  Long cachedItemsExpiry = Long.valueOf(1);
    static final Logger logger = Logger.getLogger(Aes.class.getName());


    static  LoadingCache<String, String> aesKeyCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(cachedItemsExpiry, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    String[] parts = key.split("\\^");
                    System.out.println(parts[0]);
                    return getKeyFromHttp(parts[0],parts[1],parts[2]);
                }
            });

    static  LoadingCache<String, String> plainTextCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(cachedItemsExpiry, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    String[] parts = key.split("\\^");
                    return Helper.decrypt(parts[0],parts[1]);
                }
            });

    static  LoadingCache<String, String> bearerTokenCache = CacheBuilder.newBuilder()
            .maximumSize(2)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String BASE_URL) {
                    return  getBearerToken(BASE_URL);
                }
            });
    public  static String getKeyFromCache(String BASE_URL,String username, String id){

        String cacheKeyName =BASE_URL +"^"+ username + "^" + id;
            try {
                return aesKeyCache.get(cacheKeyName);
            } catch (ExecutionException e) {
                e.printStackTrace();
               return  "Invalid";
            }
    }


//    public static String getKeyFromHttp(String username, String id) {
//
//        String aesKey = "Invalid";
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        try {
//
//            String url = String.format(System.getenv("BASE_URL")+"/api/v1/token?id=%s&username=%s", id, username);
//            HttpGet request = new HttpGet(url);
//
//            CloseableHttpResponse closeableHttpResponse = httpClient.execute(request);
//
//            try {
//                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
//                    return "Invalid";
//                } else {
//                    HttpEntity entity = closeableHttpResponse.getEntity();
//                    ObjectMapper mapper = new ObjectMapper();
//                    Response response = mapper.readValue(EntityUtils.toString(entity), Response.class);
//                    aesKey = response.getKey();
//                    return aesKey;
//                }
//
//            } catch (IOException | ParseException e) {
//
//               e.printStackTrace();
//               return "Invalid";
//            } finally {
//                closeableHttpResponse.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//
//                httpClient.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//        }
//        return  aesKey;
//    }
//

    public  static String getKeyFromHttp(String BASE_URL,String username, String id) {

        String aesKey = "unauthorized";

        try {
            String url = String.format(BASE_URL + "/api/v1/key?id=%s&username=%s", id, username);
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", getBearerFromCache(BASE_URL));

            CloseableHttpClient httpclient = HttpClients.custom().build();
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(request);
            try {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    getBearerToken(BASE_URL);
                    System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                } else {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    ObjectMapper mapper = new ObjectMapper();
                    Response response = mapper.readValue(EntityUtils.toString(entity), Response.class);
                    aesKey = response.getKey();
                }

            } catch (IOException | ParseException e) {
                aesKey = aesKey+" IOException or ParseException error";
//                log.error(e);
                e.printStackTrace();
            } finally {
                closeableHttpResponse.close();
            }
        } catch (IOException e) {
            aesKey = aesKey+ " IOException or ParseException error";
//            log.error(e);
        }
//        log.info("AesKey: "+ aesKey);
        return aesKey;
    }


    public  static String getBearerToken(String BASE_URL){
        String bearerAuth="";
        try {
            String authUsername = "svc-bigdata-admin";
            String authPassword = "lyOdmSBwCsZnD4dLnOAE";
//            authPassword="Mama@123$Ken#";
//            authUsername="Kenneth.Chirchir";
            String url = String.format(BASE_URL+ "/access/token");
            String creds = "Basic "+ Base64.getEncoder().encodeToString( (authUsername+":"+authPassword).getBytes());
            HttpPost request = new HttpPost(url);
            request.addHeader("Authorization", creds);
            CloseableHttpClient httpclient = HttpClients.custom().build();
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(request);

            try {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                    bearerAuth= "Wrong kms user Credentials";

                } else {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    ObjectMapper mapper = new ObjectMapper();
                    Token response = mapper.readValue(EntityUtils.toString(entity), Token.class);
                    bearerAuth= "Bearer "+ response.getToken();
                }
            } catch (IOException | ParseException e) {
                bearerAuth= "Get Bearer Error";
//                log.error("Kms: {}",e);
            } finally {
                closeableHttpResponse.close();
//                bearerAuth= "Get Bearer Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
//            log.error("Kms: {}",e);
            bearerAuth= "Get Bearer Error";
        }
        return  bearerAuth;

    }

    public static String getBearerFromCache(String BASE_URL){
        try {
            return bearerTokenCache.get(BASE_URL);
        } catch (ExecutionException e) {
            e.printStackTrace();
//            log.error(e);
            return "getBearerFromCache: unauthorized";
        }
    }
    public static  String getPlaintextFromCache(String cipherText, String aesKey){
        String cacheKeyName = cipherText + "^" + aesKey;

        try {
            return plainTextCache.get(cacheKeyName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return  "Invalid";
        }
    }

    /**
     * @param plainText plaintext
     * @param id        private key
     * @return Base64 Encoded cipherTextString
     */
    public static String encrypt(String BASE_URL,String plainText, String username, String id) {

        String key = getKeyFromCache(BASE_URL,username, id);

        if (key.equals("Invalid"))
            return "Invalid";
        logger.info("Key: "+key);
        return  Helper.encrypt(plainText,key);
    }


    public static String decrypt(String BASE_URL,String cipherText, String username, String id) {

        String key = getKeyFromCache(BASE_URL,username, id);

        if (key.equals("Invalid"))
            return "Invalid";
        else
            return  getPlaintextFromCache(cipherText,key);
    }

}
