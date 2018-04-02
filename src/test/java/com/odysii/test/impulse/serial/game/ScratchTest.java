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
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;

public class ScratchTest extends ImpulseTestHelper {

    private final String RUN_IMPULSE = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_imulse_scratch.exe\"";
    private Game game;
    private String gameID,placementID;
    private final int WAIT = 20000;
    private SerialMessageGenerator generator;
    private DBHandler dbHandler = null;
    private final String PLU1 = "1030030319";
    private final String PLU2 = "028400026864";
    private String selectQuery = "SELECT [Id],[ChannelId],[SiteId],[ProjectId],[TransactionId],[GameTime],[GameDate],[GameId],[GameType]" +
            ",[GameStatus],[GivenItemCode] FROM [DW_qa].[dbo].[GameJournal]";
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
        wait(WAIT);
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
    @Ignore//(priority = 2)
    public void _002_verifyConfirmRewardOnlyWinPercent(){
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
        List<String> list = new ArrayList<>();
        list.add("game_reward_body3");
        list.add("game_reward_body4");
        jsonObject = game.createRewardProps(gameID,list,RewardType.REWARD_CHOICES,"reward_choices");
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
    @Ignore//(priority = 3)
    public void _003_verifyGamePlayedWithWinUPC() {
        JSONObject jsonObject = game.createGame("winUPC", "028400026864");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        List<String> list = new ArrayList<>();
        list.add("game_reward_body1");
        list.add("game_reward_body2");
        jsonObject = game.createRewardProps(gameID,list,RewardType.IMAGE_POOL,"image_pools");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        List<String> list2 = new ArrayList<>();
        list2.add("game_reward_body3");
        list2.add("game_reward_body4");
        jsonObject = game.createRewardProps(gameID,list2,RewardType.REWARD_CHOICES,"reward_choices");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
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
    @Ignore//(priority = 4)
    public void _004_verifyGamePlayedNoConfirmReward(){
        JSONObject jsonObject = game.createGame("confirmReward","false");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        List<String> list = new ArrayList<>();
        list.add("game_reward_body1");
        list.add("game_reward_body2");
        jsonObject = game.createRewardProps(gameID,list,RewardType.IMAGE_POOL,"image_pools");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        List<String> list2 = new ArrayList<>();
        list2.add("game_reward_body3");
        list2.add("game_reward_body4");
        jsonObject = game.createRewardProps(gameID,list2,RewardType.REWARD_CHOICES,"reward_choices");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
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
    @Ignore//(priority = 5)
    public void _005_verifyConfirmRewardOnlyWinPercentNoThanks(){
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        List<String> list = new ArrayList<>();
        list.add("game_reward_body1");
        list.add("game_reward_body2");
        jsonObject = game.createRewardProps(gameID,list,RewardType.IMAGE_POOL,"image_pools");
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        List<String> list2 = new ArrayList<>();
        list2.add("game_reward_body3");
        list2.add("game_reward_body4");
        jsonObject = game.createRewardProps(gameID,list2,RewardType.REWARD_CHOICES,"reward_choices");
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
        runCmdCommand(game.getNoThanksBtn());
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
        assertNull(actual);
    }
    @Ignore//(priority = 6)
    public void _006_verifyGamePlayedWithWinUPCNoThanks() {
        JSONObject jsonObject = game.createGame("winUPC", "028400026864");
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        List<String> list = new ArrayList<>();
        list.add("game_reward_body1");
        list.add("game_reward_body2");
        jsonObject = game.createRewardProps(gameID,list,RewardType.IMAGE_POOL,"image_pools");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
        jsonObject = game.createPlacement("placement_targeted_body");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for game!");
        placementID = jsonObject.get("id").toString();
        jsonObject = game.linkPlacement(gameID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for game!");
        List<String> list2 = new ArrayList<>();
        list2.add("game_reward_body3");
        list2.add("game_reward_body4");
        jsonObject = game.createRewardProps(gameID,list2,RewardType.REWARD_CHOICES,"reward_choices");
        assertEquals(jsonObject.get("status"), "Success", "Failed to create reward for game!");
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
        assertNull(actual);
    }
    @Test(priority = 2)
    public void _007_verifyRewardMatches(){
        PropertyLoader loader = new PropertyLoader();
        Properties properties = loader.loadPropFile("rewardChoicesMatches.properties");
        List<String> list = new ArrayList<>();
        list.add(properties.getProperty("match_reward_body"));
        JSONObject jsonObject = game.createGame();
        assertEquals(jsonObject.get("status"),"Success","Failed to create game!");
        gameID = jsonObject.get("id").toString();
        List<String> list2 = new ArrayList<>();
        list2.add("game_reward_body1");
        list2.add("game_reward_body2");
        jsonObject = game.createRewardProps(gameID,list2,RewardType.IMAGE_POOL,"image_pools");
        assertEquals(jsonObject.get("status"),"Success","Failed to create reward for game!");
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
        runCmdCommand(game.getPhoneNumber());
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
