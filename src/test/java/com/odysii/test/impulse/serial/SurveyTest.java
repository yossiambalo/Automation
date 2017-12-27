package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.Survey;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SurveyTest {

    private String surveyID,placementID;

    @BeforeClass
    public void setUp(){
        Survey survey = new Survey();
        //create survey
        JSONObject jsonObject = survey.createSurvey();
        assertEquals(jsonObject.get("status"),"Success","Failed to create survey!");
        surveyID = jsonObject.get("id").toString();
        //create options for survey
        jsonObject = survey.createOption(surveyID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        //create placement for survey
        jsonObject = survey.createPlacement();
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for survey!");
        placementID = jsonObject.get("id").toString();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //link placement to survey
        jsonObject = survey.linkPlacement(surveyID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
        //delete survey
//        jsonObject = survey.deleteSurvey(surveyID);
//        assertEquals(jsonObject.get("status"),"Success","Failed to delete survey!");
//        //delete placement
//        jsonObject = survey.deletePlacement(placementID);
//        assertEquals(jsonObject.get("status"),"Success","Failed to delete placement of survey!");

    }

    @Test
    public void validSurveyInImpulse(){
        System.out.println("dddd");
    }
}
