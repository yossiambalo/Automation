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
    private String surveyRoute, surveyOptionRoute,surveyOptionBody1,surveyOptionBody2;
    private List<String> surveyOptionList;

    //const
    public Survey(){
        init();
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("survey.properties");
        surveyRoute = properties.getProperty("surveyRoute");
        createSurveyBody = properties.getProperty("create_survey_body");
        surveyOptionRoute = properties.getProperty("survey_option_route");
        surveyOptionList = new ArrayList<>();
        surveyOptionList.add(properties.getProperty("survey_option_body1"));
        surveyOptionList.add(properties.getProperty("survey_option_body2"));
    }

    public JSONObject createSurvey(){
        String url = cloudMIUri+ surveyRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(createSurveyBody);
        return JsonHandler.stringToJson(result);
    }
    public void deleteSurvey(String uri,CloudMIUser user,String surveyID){
        String url = uri+ surveyRoute +"/"+surveyID+"?ProjectId="+user.getProjectID()+"&UserEmail="+user.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        requestUtil.deleteRequest();
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
    public JSONObject createPlacement(){
        Placement placement = new Placement();
        String url = cloudMIUri+ surveyRoute+placement.getCreateRoute() +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.postRequest(placement.getBody());

        return JsonHandler.stringToJson(result);
    }
}
