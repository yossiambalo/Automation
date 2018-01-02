package com.odysii.api.cloudMI;

import com.odysii.api.MediaType;
import com.odysii.api.util.RequestUtil;
import com.odysii.general.JsonHandler;
import com.odysii.general.PropertyLoader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Survey extends CloudMI {
    private String createSurveyBody;
    private String surveyRoute, surveyOptionRoute;
    private List<String> surveyOptionList;
    private Properties properties;

    //const
    public Survey(String propFile,String surveyRoute,String surveyBody,String surveyOptionRoute,String optionBody1,String optionBody2){
        init();
        PropertyLoader propertyLoader = new PropertyLoader();
        this.properties = propertyLoader.loadPropFile(propFile);
        this.surveyRoute = properties.getProperty(surveyRoute);
        this.createSurveyBody = properties.getProperty(surveyBody);
        this.surveyOptionRoute = properties.getProperty(surveyOptionRoute);
        this.surveyOptionList = new ArrayList<>();
        this.surveyOptionList.add(properties.getProperty(optionBody1));
        this.surveyOptionList.add(properties.getProperty(optionBody2));
    }

    public JSONObject createSurvey(){
        String url = cloudMIUri+ surveyRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(createSurveyBody);
        return JsonHandler.stringToJson(result);
    }
    public JSONObject deleteSurvey(String surveyID){
        String url = cloudMIUri+ surveyRoute +"/"+surveyID+"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.deleteRequest();
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createOption(String surveyID){
        String url = cloudMIUri+ surveyOptionRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = "";
        JSONObject jsonObject = null;
        for (String body : surveyOptionList){
            jsonObject = JsonHandler.stringToJson(body);
            jsonObject.put("survey_id",surveyID);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }

    /**
     * Add options for survey
     * @param surveyID
     * @param optionProps: options names according properties file
     * @return: JSONObject
     */
    public JSONObject addOption(String surveyID,List<String> optionProps){
        String url = cloudMIUri+ surveyOptionRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = "";
        JSONObject jsonObject = null;
        for (String optionProp : optionProps){
            jsonObject = JsonHandler.stringToJson(properties.getProperty(optionProp));
            jsonObject.put("survey_id",surveyID);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createPlacement(){
        Placement placement = new Placement();
        String url = cloudMIUri+ surveyRoute+placement.getCreateRoute() +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(placement.getBody());

        return JsonHandler.stringToJson(result);
    }
    public JSONObject linkPlacement(String surveyID,String placementID){
        Placement placement = new Placement();
        String url = cloudMIUri+ surveyRoute+"/"+surveyID+placement.getAddRoute() +"?ProjectId="+projectID+"&placement_id="+placementID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_X_URL_ENCODED);
        StringBuffer result = requestUtil.getRequest();

        return JsonHandler.stringToJson(result.toString());
    }
    public JSONObject deletePlacement(String placementID){
        Placement placement = new Placement();
        String url = cloudMIUri+ surveyRoute+placement.getDeletePlacementRoute() +"?ProjectId="+projectID+"&placement_id="+placementID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_X_URL_ENCODED);
        String result = requestUtil.deleteRequest();

        return JsonHandler.stringToJson(result);
    }
}
