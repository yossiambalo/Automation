package com.odysii.api.cloudMI.survey;

import com.odysii.api.MediaType;
import com.odysii.api.cloudMI.CloudMI;
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
    private List<String> surveyOptionBodyList;

    public Properties getProperties() {
        return properties;
    }

    private Properties properties;

    //const
    public Survey(String propFile){
        PropertyLoader propertyLoader = new PropertyLoader();
        this.properties = propertyLoader.loadPropFile(propFile);
        this.surveyRoute = properties.getProperty("surveyRoute");
        this.createSurveyBody = properties.getProperty("create_survey_body");
        this.surveyOptionRoute = properties.getProperty("survey_option_route");
        this.surveyOptionBodyList = new ArrayList<>();
        this.surveyOptionBodyList.add(properties.getProperty("survey_option_body1"));
        this.surveyOptionBodyList.add(properties.getProperty("survey_option_body2"));
        init(surveyRoute);
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
        JSONObject jsonObject2 = null;
        for (String optionBody : surveyOptionBodyList){
            jsonObject = JsonHandler.stringToJson(optionBody);
            jsonObject2 = JsonHandler.stringToJson(optionBody).getJSONObject("survey_option").put("survey_id",surveyID);
            jsonObject.put("survey_option",jsonObject2);
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
        JSONObject jsonObject2 = null;
        for (String optionProp : optionProps){
            String body = properties.getProperty(optionProp);
            jsonObject = JsonHandler.stringToJson(body);
            jsonObject2 = JsonHandler.stringToJson(body).getJSONObject("survey_option").put("survey_id",surveyID);
            jsonObject.put("survey_option",jsonObject2);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }
}
