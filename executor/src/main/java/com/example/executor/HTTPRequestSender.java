package com.example.executor;

import com.example.executor.enums.HTTPMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Sends an HTTP request with the given URL.
 */
public class HTTPRequestSender {
    private Logger logger = Logger.getLogger(getClass().getName());
    /**
     * Sends an HTTP request with the given parameters.
     * @param url address of the endpoint
     * @param message message to be sent
     * @param method defines the type of HTTP request (GET, POST etc.)
     * @return the response from the endpoint as a String
     */
    public String sendRequest(URL url, String message, HTTPMethod method){

        String responseString=null;
        try{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            byte[] input = message.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();
            responseString = response.toString();
        } catch (IOException e){
            logger.log(Level.SEVERE,e.getMessage());
        }
        return responseString;
    }
}
