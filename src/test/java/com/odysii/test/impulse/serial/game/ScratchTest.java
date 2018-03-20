package com.odysii.test.impulse.serial.game;

import com.odysii.api.cloudMI.game.Game;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import com.odysii.test.impulse.helper.RewardType;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

public class ScratchTest extends ImpulseTestHelper {

    private final String RUN_IMPULSE = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_imulse_scratch.exe\"";
    private Game game;
    private String gameID,placementID;
    private final int WAIT = 15000;
    private SerialMessageGenerator generator;
    private DBHandler dbHandler = null;
    private final String LOSE_STATUS = "Lose";
    private final String WON_STATUS = "WonRedeemed";
    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        assertNotEquals(generator.doGetRequest(atbListenerUrl),"Failed","AddToBasket Service Not responding!");
        game = new Game("scratch.properties");
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID, RewardType.IMAGE_POOL);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
    }
    @Test(priority = 1)
    public void _001_verifyImagePoolOnlyWinPercent(){
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(game.getPlayGameScript());
        wait(10000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        String query = "SELECT [Id],[ChannelId],[SiteId],[ProjectId],[TransactionId],[GameTime],[GameDate],[GameId],[GameType]" +
                ",[GameStatus] FROM [DW_qa].[dbo].[GameJournal] where GameId='"+gameID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,10);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,10);
            timeOut++;
        }
        System.out.println("================GameID: "+gameID+" Actual gameID is: "+actual+"======================");
        assertEquals(actual,WON_STATUS);
    }
    @Test(priority = 2)
    public void _002_verifyConfirmRewardOnlyWinPercent(){
        List<String> list = new ArrayList<>();
        list.add("game_reward_body3");
        list.add("game_reward_body4");
        JSONObject jsonObject = game.createRewardProps(gameID,list,RewardType.REWARD_CHOICES);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        runCmdCommand(RUN_IMPULSE);
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        runCmdCommand(game.getPlayGameScript());
        wait(10000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        String query = "SELECT [Id],[ChannelId],[SiteId],[ProjectId],[TransactionId],[GameTime],[GameDate],[GameId],[GameType]" +
                ",[GameStatus] FROM [DW_qa].[dbo].[GameJournal] where GameId='"+gameID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,10);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,10);
            timeOut++;
        }
        System.out.println("================GameID: "+gameID+" Actual gameID is: "+actual+"======================");
        assertEquals(actual,WON_STATUS);
    }
    @AfterClass
    public void tearDown(){
        JSONObject jsonObject = game.deleteGame(gameID);
        assertEquals(jsonObject.get("status"),"Success");
        runCmdCommand(closeImpulseRunnerScript);
        if (dbHandler != null) {
            dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[GameJournal] where ProjectId='2727'");
            dbHandler.closeConnection();
        }
    }
}
