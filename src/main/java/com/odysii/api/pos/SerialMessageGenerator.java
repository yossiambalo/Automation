package com.odysii.api.pos;

public class SerialMessageGenerator extends MessageGenerator {

    private final String uri = "VirtualWriteMessage";
    public void doPostRequest(String body){
        super.doPostRequest(body,uri);
    }

}
