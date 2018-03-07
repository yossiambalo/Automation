package com.odysii.test.impulse.serial.game;

import com.odysii.api.cloudMI.game.Game;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SpinTheWheelTest extends ImpulseTestHelper {
    private Game game;
    private String gameID,placementID;
    private final int WAIT = 15000;
    private SerialMessageGenerator generator;
    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        game = new Game("game.properties");
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
    }
    @Test
    public void test(){
        runCmdCommand(impulseRunnerScript);
        wait(WAIT);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(game.getPlayGameScript());
        wait(10000);
        runCmdCommand(game.getGetRewardScript());
        wait(10000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        System.out.println("It is a test!");
    }
    @AfterClass
    public void tearDown(){
        JSONObject jsonObject = game.deleteGame(gameID);
        assertEquals(jsonObject.get("status"),"Success");
    }
}
