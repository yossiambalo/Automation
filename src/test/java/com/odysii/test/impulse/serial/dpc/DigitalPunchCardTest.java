package com.odysii.test.impulse.serial.dpc;

import com.odysii.api.cloudMI.dpc.DigitalPunchCard;
import com.odysii.api.pos.MessageGenerator;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class DigitalPunchCardTest extends ImpulseTestHelper{
    private MessageGenerator generator;
    private DigitalPunchCard digitalPunchCard;
    private String dpcID,placementID;

    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        assertNotEquals(generator.doGetRequest(atbListenerUrl),"Failed","AddToBasket Service Not responding!");
        digitalPunchCard = new DigitalPunchCard("digital_punch_card.properties");
        JSONObject jsonObject = digitalPunchCard.createDPC();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        dpcID = jsonObject.get("id").toString();
        jsonObject = digitalPunchCard.createRedemption(dpcID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = digitalPunchCard.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = digitalPunchCard.linkPlacement(dpcID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
    }
    @Test
    public void test(){
        System.out.println("It is a test!");
    }
}
