package com.odysii.api.cloudMI;

import com.odysii.api.MediaType;
import com.odysii.api.util.RequestUtil;

public class Survey {
    private String token = "Token token=efb06860-11d2-4fa6-a440-13905f5c8e9b";
    private String surveyRoute = "/content/surveys";

    //const
    public Survey(String token, String surveyRoute){
        this.token = token;
        this.surveyRoute = surveyRoute;
    }

    public void createSurvey(String uri, String body, CloudMIUser user,String projectID){
        String url = uri+ surveyRoute +"?ProjectId="+projectID+"&UserEmail="+user.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        requestUtil.postRequest(body);
    }
    public void deleteSurvey(String uri,CloudMIUser user,String surveyID){
        String url = uri+ surveyRoute +"/"+surveyID+"?ProjectId="+user.getProjectID()+"&UserEmail="+user.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        requestUtil.deleteRequest();
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
