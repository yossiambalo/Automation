package com.odysii.test.impulse.serial.game;

import com.odysii.api.cloudMI.game.Game;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SpinTheWillTest extends ImpulseTestHelper {
    private Game game;
    private String gameID,placementID;
    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        game = new Game("game.properties");
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement();
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
    }
    @Test
    public void test(){
        System.out.println("It is a test!");
    }
}
