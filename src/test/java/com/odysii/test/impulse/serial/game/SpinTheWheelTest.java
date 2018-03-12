package com.odysii.test.impulse.serial.game;

import com.odysii.api.cloudMI.game.Game;
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

public class SpinTheWheelTest extends ImpulseTestHelper {
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
    public void _001_verifyOnlyWinPercent(){
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
    @Test
    public void _002_verifyOnlyLosePercent(){
        JSONObject jsonObject = game.createGame("winPercent","0");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        //click to play game
        runCmdCommand(game.getPlayGameScript());
        wait(10000);
        //click to get the reward
        runCmdCommand(game.getGetRewardScript());
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
        assertEquals(actual,LOSE_STATUS);
    }
    @Test
    public void _003_verifyGamePlayedWithoutListItems(){
        JSONObject jsonObject = game.createGame("winUPC","028400026864");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        wait(WAIT);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(2000);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(2000);
        //click to play game
        runCmdCommand(game.getPlayGameScript());
        wait(10000);
        //click to get the reward
        runCmdCommand(game.getGetRewardScript());
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
    @AfterMethod
    public void afterMethod(){
        JSONObject jsonObject = game.deleteGame(gameID);
        assertEquals(jsonObject.get("status"),"Success");
        if (dbHandler != null) {
            dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[GameJournal] where ProjectId='2727'");
            dbHandler.closeConnection();
        }
    }
    @AfterClass
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
    }
}
