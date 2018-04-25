package com.odysii.test.impulse.serial.dpc;

import com.odysii.api.cloudMI.dpc.DigitalPunchCard;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class DigitalPunchCardTest extends ImpulseTestHelper{
    private SerialMessageGenerator generator;
    private DigitalPunchCard digitalPunchCard;
    private String dpcID,placementID;
    private final int WAIT = 15000;
    private final String RUN_IMPULSE = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_imulse_scratch.exe\"";
    private final String COFFEE_CLUB_BTN = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && coffee_club_btn.exe\"";
    private final String COFFEE_CLUB_COUPON_BTN = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && coffee_club_coupon_btn.exe\"";
    private final String FILL_PHONE_NUM_SCRIPT = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && scratch_phone_number.exe\"";
    private final String REDMPTION_SCRIPT = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && coupon_redemption_btn.exe\"";
    private String selectQuery = "SELECT [Id],[ProjectId],[Phone],[EventType],[ItemCode],[Quantity],[CampaignId],[CouponId] FROM [DW_qa].[dbo].[LoyaltyJournal]";
    private DBHandler dbHandler;
    private String campaignID = "628";
    private final String PLACEMENT_ID = "3000";

    @BeforeClass
    public void setUp(){
        JSONObject jsonObject;
        init(POSType.PASSPORT_SERIAL);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        assertNotEquals(generator.doGetRequest(atbListenerUrl),"Failed","AddToBasket Service Not responding!");
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        runCmdCommand(COFFEE_CLUB_COUPON_BTN);
        wait(5000);
        runCmdCommand(REDMPTION_SCRIPT);
        wait(7000);
        generator.doPostRequest(customer.getEndTransaction());
        digitalPunchCard = new DigitalPunchCard("digital_punch_card.properties");
//        JSONObject jsonObject = digitalPunchCard.createDPC("campaign_type","Purchase");
//        assertEquals(jsonObject.get("status"),"Success","Failed to create DPC!");
//        dpcID = jsonObject.get("id").toString();
//        jsonObject = digitalPunchCard.createRedemption(dpcID);
//        assertEquals(jsonObject.get("status"),"Success","Failed to create redemption for DPC!");
//        jsonObject = digitalPunchCard.createPlacement("placement_targeted_body");
//        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for DPC!");
//        placementID = jsonObject.get("id").toString();
        jsonObject = digitalPunchCard.linkPlacement(campaignID,PLACEMENT_ID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for DPC!");
    }
    @Test
    public void _001_valid_purchase_campaign(){
        String purchaseList = "1030093019";
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        //Add item
        generator.doPostRequest(customer.getAddItem(purchaseList));
        wait(2000);
        generator.doPostRequest(customer.getAddItem(purchaseList));
        wait(2000);
        generator.doPostRequest(customer.getAddItem(purchaseList));
        wait(2000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        wait(5000);
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        runCmdCommand(COFFEE_CLUB_COUPON_BTN);
        wait(5000);
        runCmdCommand(REDMPTION_SCRIPT);
        wait(10000);
        generator.doPostRequest(customer.getEndTransaction());
        String query = selectQuery+"  where CampaignId='"+ campaignID +"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,7);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 12)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,7);
            timeOut++;
        }
        assertEquals(actual, campaignID);
    }
    @Test
    public void _002_valid_spent_campaign(){
        campaignID = "624";
        digitalPunchCard.linkPlacement(campaignID,PLACEMENT_ID);
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        generator.doPostRequest(customer.getTotal());
        wait(2000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        wait(5000);
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        runCmdCommand(COFFEE_CLUB_COUPON_BTN);
        wait(5000);
        runCmdCommand(REDMPTION_SCRIPT);
        wait(10000);
        generator.doPostRequest(customer.getEndTransaction());
        String query = selectQuery+"  where CampaignId='"+ campaignID +"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,7);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 12)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,7);
            timeOut++;
        }
        assertEquals(actual, campaignID);
    }
    @Test
    public void _003_valid_visit_campaign(){
        campaignID = "619";
        digitalPunchCard.linkPlacement(campaignID,PLACEMENT_ID);
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        wait(5000);
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        generator.doPostRequest(customer.getAddItem());
        wait(1000);
        digitalPunchCard.unlinkPlacement(campaignID, PLACEMENT_ID);
        wait(2000);
        runCmdCommand(COFFEE_CLUB_BTN);
        wait(5000);
        runCmdCommand(FILL_PHONE_NUM_SCRIPT);
        wait(10000);
        runCmdCommand(COFFEE_CLUB_COUPON_BTN);
        wait(5000);
        runCmdCommand(REDMPTION_SCRIPT);
        wait(10000);
        generator.doPostRequest(customer.getEndTransaction());
        String query = selectQuery+"  where CampaignId='"+ campaignID +"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,7);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 12)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,7);
            timeOut++;
        }
        assertEquals(actual, campaignID);
    }
    @AfterMethod
    public void clean(){
        digitalPunchCard.unlinkPlacement(campaignID, PLACEMENT_ID);
    }
    @AfterClass
    public void tearDown(){
        if (dbHandler != null) {
            dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[LoyaltyJournal] where ProjectId = '2727'");
            dbHandler.closeConnection();
        }
        runCmdCommand(closeImpulseRunnerScript);
    }
}

