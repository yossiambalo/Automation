package com.odysii.api.util;

import com.google.common.net.MediaType;
import com.sun.xml.internal.ws.api.pipe.ContentType;

import java.awt.*;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestHelper {

    protected Map<String,String> getHeaders;
    protected Map<String,String> postHeaders;
    protected Map<String,String> deleteHeaders;

    protected void setGetHeaders(String token, String mediaType){
        getHeaders = new HashMap<String, String>();
        getHeaders.put("Authorization",token);
        getHeaders.put("Content-Type",mediaType);
    }

    protected void setPostHeaders(String token, String mediaType){
        postHeaders = new HashMap<String, String>();
        postHeaders.put("Authorization",token);
        postHeaders.put("Content-Type", mediaType);
    }
    protected void setDeleteHeaders(String token, String mediaType){
        deleteHeaders = new HashMap<String, String>();
        deleteHeaders.put("Authorization",token);
        deleteHeaders.put("Content-Type", mediaType);
    }
}
