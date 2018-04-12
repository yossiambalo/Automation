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
        JSONObject jsonObject = digitalPunchCard.createDPC("campaign_type","Purchase");
        assertEquals(jsonObject.get("status"),"Success","Failed to create DPC!");
        dpcID = jsonObject.get("id").toString();
//        jsonObject = digitalPunchCard.createRedemption(dpcID);
//        assertEquals(jsonObject.get("status"),"Success","Failed to create redemption for DPC!");
        jsonObject = digitalPunchCard.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for DPC!");
        placementID = jsonObject.get("id").toString();
        jsonObject = digitalPunchCard.linkPlacement(dpcID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for DPC!");
    }
    @Test
    public void test(){
        System.out.println("It is a test!");
    }
}
