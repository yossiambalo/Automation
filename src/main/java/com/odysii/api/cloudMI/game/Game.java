package com.odysii.api.cloudMI.game;

import com.odysii.api.MediaType;
import com.odysii.api.cloudMI.CloudMI;
import com.odysii.api.cloudMI.Placement;
import com.odysii.api.util.RequestUtil;
import com.odysii.general.JsonHandler;
import com.odysii.general.PropertyLoader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Game extends CloudMI {

    private String createGameBody;
    private String gameRoute, gameRewardRoute;
    private List<String> gameRewardBodyList;

    public Properties getProperties() {
        return properties;
    }

    private Properties properties;
    public Game(String propFile){
        init();
        PropertyLoader propertyLoader = new PropertyLoader();
        this.properties = propertyLoader.loadPropFile(propFile);
        this.gameRoute = properties.getProperty("gameRoute");
        this.createGameBody = properties.getProperty("create_game_body");
        this.gameRewardRoute = properties.getProperty("game_reward_route");
        this.gameRewardBodyList = new ArrayList<>();
        this.gameRewardBodyList.add(properties.getProperty("game_reward_body1"));
        this.gameRewardBodyList.add(properties.getProperty("game_reward_body2"));
    }
    public JSONObject createGame(){
        String url = cloudMIUri+ gameRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(createGameBody);
        return JsonHandler.stringToJson(result);
    }
    public JSONObject deleteGame(String surveyID){
        String url = cloudMIUri+ gameRoute +"/"+surveyID+"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.deleteRequest();
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createReward(String gameID){
        String url = cloudMIUri+ gameRewardRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = "";
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        for (String optionBody : gameRewardBodyList){
            jsonObject = JsonHandler.stringToJson(optionBody);
            jsonObject2 = JsonHandler.stringToJson(optionBody).getJSONObject("RewardChoices").put("parent_id",gameID);
            jsonObject.put("RewardChoices",jsonObject2);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }

    /**
     * Add options for survey
     * @param gameID
     * @param optionProps: options names according properties file
     * @return: JSONObject
     */
    public JSONObject addReward(String gameID,List<String> optionProps){
        String url = cloudMIUri+ gameRewardRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = "";
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        for (String optionProp : optionProps){
            String body = properties.getProperty(optionProp);
            jsonObject = JsonHandler.stringToJson(body);
            jsonObject2 = JsonHandler.stringToJson(body).getJSONObject("RewardChoices").put("parent_id",gameID);
            jsonObject.put("RewardChoices",jsonObject2);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createPlacement(){
        Placement placement = new Placement();
        String url = cloudMIUri+ gameRoute +placement.getCreateRoute() +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(placement.getBody());

        return JsonHandler.stringToJson(result);
    }
    public JSONObject createPlacement(String placementProp){
        Placement placement = new Placement(placementProp);
        String url = cloudMIUri+ gameRoute +placement.getCreateRoute() +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(placement.getBody());

        return JsonHandler.stringToJson(result);
    }
    public JSONObject linkPlacement(String contentID,String placementID){
        Placement placement = new Placement();
        String url = cloudMIUri+ gameRoute +"/"+contentID+placement.getAddRoute() +"?ProjectId="+projectID+"&placement_id="+placementID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_X_URL_ENCODED);
        StringBuffer result = requestUtil.getRequest();

        return JsonHandler.stringToJson(result.toString());
    }
    public JSONObject deletePlacement(String placementID){
        Placement placement = new Placement();
        String url = cloudMIUri+ gameRoute +placement.getDeletePlacementRoute() +"?ProjectId="+projectID+"&placement_id="+placementID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_X_URL_ENCODED);
        String result = requestUtil.deleteRequest();
        return JsonHandler.stringToJson(result);
    }
}