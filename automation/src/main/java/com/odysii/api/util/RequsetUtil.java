package com.odysii.api.util;

public class RequsetUtil extends RequestHelper {

    public RequsetUtil(String token){
        setHeaders(token);
    }
    //ToDo: add following methods: get,post,update and delete
}
