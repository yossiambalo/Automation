package com.odysii.test.impulse.serial.game;

import com.odysii.api.cloudMI.game.Game;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import com.odysii.test.impulse.helper.RewardType;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class SpinTheWheelTest extends ImpulseTestHelper {
    private Game game;
    private String gameID,placementID;
    private final int WAIT = 20000;
    private SerialMessageGenerator generator;
    private DBHandler dbHandler = null;
    private final String PLU1 = "1030093019";
    private final String PLU2 = "028400026864";
    private String selectQuery = "SELECT [Id],[ChannelId],[SiteId],[ProjectId],[TransactionId],[GameTime],[GameDate],[GameId],[GameType]" +
            ",[GameStatus],[GivenItemCode] FROM [DW_qa].[dbo].[GameJournal]";
    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        game = new Game("spin_the_wheel.properties");
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID,"RewardChoices");
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
    }
    @Test
    public void _001_verifyOnlyWinPercent(){
        wait(WAIT);
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
        String query = selectQuery+"  where GameId='"+gameID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,11);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 10)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,11);
            timeOut++;
        }
        if (actual.equals(PLU1)) {
            assertEquals(actual, PLU1);
        }else {
            assertEquals(actual, PLU2);
        }
    }
    @Test
    public void _002_verifyOnlyLosePercent(){
        JSONObject jsonObject = game.createGame("winPercent","0");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID,"RewardChoices");
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
        String query = selectQuery+"  where GameId='"+gameID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,11);
        int timeOut = 0;
        while((actual != null && timeOut < 10)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,11);
            timeOut++;
        }
        System.out.println("================GameID: "+gameID+" Actual gameID is: "+actual+"======================");
        assertNull(actual);
    }
    @Test
    public void _003_verifyGamePlayedWithWinUPC() {
        JSONObject jsonObject = game.createGame("winUPC", "028400026864");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID, RewardType.REWARD_CHOICES);
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID, placementID);
        assertEquals(jsonObject.get("status"), "Success", "Failed to link placement for game!");
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
        String query = selectQuery + "  where GameId='" + gameID + "'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query, 11);
        int timeOut = 0;
        while ((StringUtils.isEmpty(actual) && timeOut < 10)) {
            wait(5000);
            actual = dbHandler.executeSelectQuery(query, 11);
            timeOut++;
        }
    }
        @Test
        public void _004_verifyGamePlayedNoConfirmReward(){
            JSONObject jsonObject = game.createGame("confirmReward","false");
            assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
            gameID = jsonObject.get("id").toString();
            jsonObject = game.createReward(gameID, RewardType.REWARD_CHOICES);
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
            //finish transaction
            generator.doPostRequest(customer.getEndTransaction());
            String query = selectQuery+"  where GameId='"+gameID+"'";
            dbHandler = new DBHandler();
            String actual = dbHandler.executeSelectQuery(query,11);
            int timeOut = 0;
            while((StringUtils.isEmpty(actual) && timeOut < 10)){
                wait(5000);
                actual = dbHandler.executeSelectQuery(query,11);
                timeOut++;
            }
            System.out.println("================Expected item : "+PLU1+" or "+PLU2+" Actual is: "+actual+"======================");
        if (actual.equals(PLU1)) {
            assertEquals(actual, PLU1);
        }else {
            assertEquals(actual, PLU2);
        }
    }
    @Test
    public void _005_verifyGamePlayedWithWinUPCNoThanks() {
        JSONObject jsonObject = game.createGame("winUPC", "028400026864");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create game!");
        gameID = jsonObject.get("id").toString();
        jsonObject = game.createReward(gameID, RewardType.REWARD_CHOICES);
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID, placementID);
        assertEquals(jsonObject.get("status"), "Success", "Failed to link placement for game!");
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
        runCmdCommand(game.getNoThanksBtn());
        wait(10000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        String query = selectQuery + "  where GameId='" + gameID + "'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query, 11);
        int timeOut = 0;
        while ((StringUtils.isEmpty(actual) && timeOut < 10)) {
            wait(5000);
            actual = dbHandler.executeSelectQuery(query, 11);
            timeOut++;
        }
        assertNull(actual,"1030093019");
    }
    @Ignore
    public void _006_verifyRewardMatches(){
        PropertyLoader loader = new PropertyLoader();
        Properties properties = loader.loadPropFile("rewardChoicesMatches.properties");
        List<String> list = new ArrayList<>();
        list.add(properties.getProperty("match_reward_body"));
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        //Create reward matches
        jsonObject = game.createReward(gameID,list,RewardType.REWARD_CHOICES_MATCHES,"reward_choices_matches");
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        String rewardID = jsonObject.get("id").toString();
        //Create reward choices group
        list.clear();
        list.add(properties.getProperty("reward_group_body"));
        jsonObject = game.createReward(rewardID,list,RewardType.REWARD_CHOICES_GROUPS,"reward_choices_groups");
        assertEquals(jsonObject.get("status"),"Success","Failed to create group for reward choice!");
        String rewardGroupID = jsonObject.get("id").toString();
        //Create reward choices list
        list.clear();
        list.add(properties.getProperty("reward_group_list"));
        list.add(properties.getProperty("reward_group_list2"));
        jsonObject = game.createReward(rewardGroupID,list,RewardType.REWARD_CHOICES_LIST,"reward_choices_list");
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward list for group!");
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
        runCmdCommand(game.getPhoneNumberScript());
        wait(15000);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        String query = selectQuery+"  where GameId='"+gameID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,10);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 10)){
            wait(5000);
            actual = dbHandler.executeSelectQuery(query,10);
            timeOut++;
        }
        System.out.println("================GameID: "+gameID+" Actual gameID is: "+actual+"======================");
        assertEquals(actual, PLU1);
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
}
