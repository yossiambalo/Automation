package com.odysii.api.pos;

public class NativeMessageGenerator extends MessageGenerator{

    private final String uri = "POSEvent";

    public void doPostRequest(String body){
        super.doPostRequest(body,uri);
    }
}
