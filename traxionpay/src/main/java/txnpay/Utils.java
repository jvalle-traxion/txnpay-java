package txnpay;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;


public final class Utils {
    private static final String BASE_URL = "https://devapi.traxionpay.com";

    /**
     * 
     * @param secretKey
     * @return Base64 encoded token
     * @throws Exception
     */
    public static String generateToken(String secretKey) throws Exception {
        if (!secretKey.isBlank()) {
            byte[] encodedBytes = Base64.getEncoder().encode(secretKey.getBytes());
            String token = new String(encodedBytes);
            return token;
        }
        throw new Exception("'secretKey' must not be null.");
    }

    /**
     * Performs HTTP requests
     * 
     * @param method
     * @param endpoint
     * @param headers
     * @param json
     * @param payload
     * @return JSONObject
     */
    public static JSONObject request(String method, String endpoint, @Nullable BasicHeader headers, @Nullable String json, @Nullable List payload) {
        JSONParser parser = new JSONParser();
        JSONObject data = new JSONObject();
        Object parsedResponse = new Object();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = BASE_URL + endpoint;
        
        // handle POST requests
        if (method == "POST") {
            HttpPost request = new HttpPost(url);
            try {
                if (headers != null) request.setHeader(headers);
                if (payload != null) request.setEntity(new UrlEncodedFormEntity(payload, Consts.UTF_8));
                if (json != null) request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

                HttpResponse response = httpclient.execute(request);

                if (payload != null) {
                    parsedResponse = response.getHeaders("Location")[0].getValue();
                    data.put("url", parsedResponse);
                } else {
                    String res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    parsedResponse = parser.parse(res);

                    if (parsedResponse instanceof JSONArray) data.put("data", parsedResponse);
                    if (parsedResponse instanceof JSONObject) data = (JSONObject) parsedResponse;
                }

            } catch (Exception e) {
                System.out.println("HTTP POST Exception: " + e);
            }
        }

        // handle GET requests
        if (method == "GET") {
            HttpGet request = new HttpGet(url);
            try {
                if (headers != null) request.setHeader(headers);

                HttpResponse response = httpclient.execute(request);
                String res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                parsedResponse = parser.parse(res);

                if (parsedResponse instanceof JSONArray) data.put("data", parsedResponse);
                if (parsedResponse instanceof JSONObject) data = (JSONObject) parsedResponse;
            } catch (Exception e) {
                System.out.println("HTTP GET Exception: " + e);
            }
        }
        
        return data;
    }

    /**
     * Validates info.
     * 
     * @param value
     * @param defaultValue
     * @return  empty string or default value if provided.
     */
    public static String getValidData(String value, String...defaultValue) {
        String defVal = defaultValue.length > 0 ? defaultValue[0] : "";
        return value == null || value.isBlank() ? defVal : value;
    }

    /**
     * Encrypts data using Sha256
     * 
     * @param data
     * @param key
     * @return encrypted data
     */
    public static String hmacSha256Digest(String data, String key) {
        String hashedData = null;
        
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            
            byte[] hashedDataByte = sha256_HMAC.doFinal(data.getBytes());
            hashedData = String.format("%032x", new BigInteger(1, hashedDataByte)); 
        } catch (Exception e) {
            System.out.println("Error:"+ e);
        }
        
        return hashedData;
    }

    public static String encodeAdditionalData(String additionalData) {
        byte[] encodedData = Base64.getEncoder().encode(additionalData.getBytes());
        String data = new String(encodedData);
        return data;
    }
}
