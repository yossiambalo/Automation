package com.odysii.api.pos;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public abstract class MessageGenerator {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private final String listenerUrl = "http://localhost:7007/OdysiiDeliveryStation/";
    private final String messageInfo = "<MessageInfo><MessageInfo UniqueID=\"4ab7f2c4-2127-455c-bd9c-3d77140bd165\" Category=\"POSAddProductMessaging\" BodyType=\"System.String\" ChannelID=\"0\" TimeStamp=\"2017-11-28T08:25:36.6188534Z\">";
    private final String endMessageInfo = "</MessageInfo></MessageInfo>";
    private final String nativeUrl = "http://localhost:7007/OdysiiDeliveryStation/";
    //const
    public MessageGenerator(){
    }

    /**
     * @param url
     * @return
     */
    public String doGetRequest(String url){
        String res = "";
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(request);
            // Get the response
            BufferedReader br;

            br = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
            String line = "";
            while ((line = br.readLine()) != null) {
                res = line;
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @param body : body
     */
    public void doPostRequest(String body, String uri){
        String input = ""+messageInfo+""+body+""+endMessageInfo+"";
        try {
            URL url = new URL(listenerUrl+uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(input.getBytes());
            outputStream.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
        }catch (MalformedURLException e){
            e.fillInStackTrace();
        }catch (IOException e){
            e.fillInStackTrace();
        }
    }

}

