package com.odysii.api.pos;

public class SerialMessageGenerator extends MessageGenerator {

    private final String route = "VirtualWriteMessage";
    private final String messageInfo = "<MessageInfo><MessageInfo UniqueID=\"4ab7f2c4-2127-455c-bd9c-3d77140bd165\" Category=\"POSAddProductMessaging\" BodyType=\"System.String\" ChannelID=\"0\" TimeStamp=\"2017-11-28T08:25:36.6188534Z\">";
    private final String endMessageInfo = "</MessageInfo></MessageInfo>";
    private final String listenerUri = "http://localhost:7007/OdysiiDeliveryStation/";

    public SerialMessageGenerator(String uri){
        super(uri);

    }
    public void doPostRequest(String body){
        body = ""+messageInfo+""+body+""+endMessageInfo+"";
        super.doPostRequest(body, route);
    }

}
