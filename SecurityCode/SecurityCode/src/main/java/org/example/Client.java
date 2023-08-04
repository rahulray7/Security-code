package org.example;

import okhttp3.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import static org.example.SecurityCode.connect_db;

public class Client {
    public static final String url = "http://localhost:9090/addUser";

    static OkHttpClient httpClient = new OkHttpClient();

    public static String encodedKey = null, encodedIV, encodedPassword;
    static KeyGenerator keyGenerator;
    static byte[] encryptedPassword, iv;
    static SecretKey secretKey;

    public static void encrpyt_pass() {
        try {
            User user = new User("user@example.com","admin");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encryptedPassword = cipher.doFinal(user.getPassword().getBytes("UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void generate_random_aes_key(){
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // key length can be 128, 192 or 256
            secretKey = keyGenerator.generateKey();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void generate_random_iv(){
        iv = new byte[16];
        ThreadLocalRandom.current().nextBytes(iv);
    }

    public static void main(String[] args) throws Exception {
            // Generate a random AES key
            generate_random_aes_key();

            // Convert the key to a base64 encoded string
            encodedKey = DatatypeConverter.printBase64Binary(secretKey.getEncoded());

            // Generate a random initialization vector
            generate_random_iv();

            // Convert the initialization vector to a base64 encoded string
            encodedIV = Base64.getEncoder().encodeToString(iv);

            // Encrypt the password using AES in CBC mode with the key and IV
            encrpyt_pass();

            // Convert the encrypted password to a base64 encoded string
            encodedPassword = Base64.getEncoder().encodeToString(encryptedPassword);

            //Function Call
            sendEncryptedKeyandIV(url, encodedKey, encodedIV);

            //Connecting to Database
            connect_db();
        }

    private static String sendEncryptedKeyandIV(String url, String encodedKey, String encodedIV){

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("encodedKey", encodedKey)
                .addFormDataPart("encodedIV", encodedIV)
                .build();

        Request request = new Request.Builder()
                .url(url)
                        .post(requestBody)
                                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}