package com.odysii.api.util;

import com.odysii.api.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class RequestUtil extends RequestHelper {

    private String token;
    private String url;
    private String mediaType;

    public RequestUtil(String token, String url){
        this.token = token;
        this.url = url;
    }
    public RequestUtil(String token, String url,String mediaType){
        this.token = token;
        this.url = url;
        this.mediaType = mediaType;
    }
    private String getUrl() {
        return url;
    }

    public int getRequest(){
        setGetHeaders(token,mediaType);
        return 0;
    }
    public String postRequest(String body){
        setPostHeaders(token,mediaType);
        String res = "";
        try {
            URL url = new URL(getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            for (Map.Entry<String,String> header : postHeaders.entrySet()){
                conn.setRequestProperty(header.getKey(),header.getValue());
            }
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to create survey: HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output = "";
            while ((output = bufferedReader.readLine()) != null){
                res = output;
            }
        }catch (MalformedURLException e){
            System.out.println(e.getMessage());
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    public int deleteRequest(){
        setDeleteHeaders(token,mediaType);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            for (Map.Entry<String,String> header : deleteHeaders.entrySet()){
                conn.setRequestProperty(header.getKey(),header.getValue());
            }
            OutputStream outputStream = conn.getOutputStream();
            outputStream.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to delete survey : HTTP error code Survey may not exist : "
                        + conn.getResponseCode());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output = "";
            while ((output = bufferedReader.readLine()) != null){
                System.out.println(output);
            }
        }catch (MalformedURLException e){
            System.out.println(e.getMessage());
        }catch (IOException e){
            System.out.println(e.getMessage());
        }finally {
            conn.disconnect();
        }
        return 0;
    }
    //ToDo: add following methods: get,post,update and delete
}
