package com.odysii.api.cloudMI;

import com.odysii.api.MediaType;
import com.odysii.api.util.RequestUtil;
import com.odysii.general.JsonHandler;
import com.odysii.general.PropertyLoader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Survey {
    private String token,cloudMIUri,projectID,createSurveyBody;// = "Token token=efb06860-11d2-4fa6-a440-13905f5c8e9b";
    private String surveyRoute, surveyOptionRoute,surveyOptionBody1,surveyOptionBody2;
    CloudMIUser cloudMIUser;
    private List<String> surveyOptionList;

    //const
    public Survey(){
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("survey.properties");
        token = properties.getProperty("token");
        surveyRoute = properties.getProperty("surveyRoute");
        cloudMIUri = properties.getProperty("coloudMI_uri");
        projectID = properties.getProperty("project_id");
        createSurveyBody = properties.getProperty("create_survey_body");
        cloudMIUri = properties.getProperty("coloudMI_uri");
        surveyOptionRoute = properties.getProperty("survey_option_route");
        cloudMIUser = new CloudMIUser(properties.getProperty("user_name"));
        surveyOptionList = new ArrayList<String>();
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
    public static void main(String[]args){
        String uri = "http://cloudmiqa.tveez.local/api/v0";
        CloudMIUser user = new CloudMIUser("yossi.ambalo@odysii.com","456123");
        String body = "{\"survey\": {\"name\": \"survey1\",\"question\": \"qqq1\",\"survey_template\": \"Image\",\"image\":\"CAFE01\",\"image_format\": \"abc\",\"background_image\":\"CAFE02\",\"background_image_format\": \"abc\"}}";
//        Survey survey = new Survey();
//        survey.createSurvey(uri,body,user);
        //survey.deleteSurvey(uri,user,"1269");

    }
}
