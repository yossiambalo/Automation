package com.odysii.api.util;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    protected Map<String,String> headers;
    protected void setHeaders(String token){
        headers = new HashMap<String, String>();
        headers.put("Authorization",token);
    }
}
