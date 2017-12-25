package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.Survey;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SurveyTest {

    private String surveyID;

    @BeforeClass
    public void setUp(){
        Survey survey = new Survey();
        JSONObject jsonObject = survey.createSurvey();
        assertEquals(jsonObject.get("status"),"Success","Failed to create survey!");
        surveyID = jsonObject.get("id").toString();
        jsonObject = survey.createOption(surveyID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
    }

    @Test
    public void validSurveyInImpulse(){
        System.out.println("dddd");
    }
}
