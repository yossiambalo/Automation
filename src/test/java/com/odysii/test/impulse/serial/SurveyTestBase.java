package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.survey.DonationSurvey;
import com.odysii.api.cloudMI.survey.Survey;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertEquals;

public class SurveyTestBase extends ImpulseTestHelper {

    protected String surveyID,placementID,surveyOptionID;
    SerialMessageGenerator generator;
    protected Survey survey;
    protected JSONObject jsonObject;
    protected DBHandler dbHandler;

    public void setUp(Class<? extends Survey> surveyClass){
        init(POSType.BULLOCH);
        try {
            survey = surveyClass.newInstance();
        } catch (InstantiationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
        //create survey
        jsonObject = survey.createSurvey();
        assertEquals(jsonObject.get("status"),"Success","Failed to create survey!");
        surveyID = jsonObject.get("id").toString();
        //create options for survey
        jsonObject = survey.createOption(surveyID);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
        //create placement for survey
        jsonObject = survey.createPlacement();
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for survey!");
        placementID = jsonObject.get("id").toString();
        //link placement to survey
        wait(3000);
        jsonObject = survey.linkPlacement(surveyID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
    }
    @AfterClass
    public void tearDown(){
        String query = "delete FROM [DW_qa].[dbo].[SurveyJournal] where [ProjectId]='2727'";
        survey.deletePlacement(placementID);
        survey.deleteSurvey(surveyID);
        dbHandler.executeDeleteQuery(query);
        dbHandler.closeConnection();
    }
}
