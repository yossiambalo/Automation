package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.survey.Survey;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;


import static org.testng.Assert.assertEquals;

public class SurveyTestBase extends ImpulseTestHelper {

    protected String surveyID,placementID,surveyOptionID;
    SerialMessageGenerator generator;
    protected Survey survey;
    protected JSONObject jsonObject;
    protected DBHandler dbHandler;
    protected final String QUERY = "delete FROM [DW_qa].[dbo].[SurveyJournal] where [ProjectId]='2727'";

    public Survey setUp(String surveyProp,boolean enablePlacement){
        //ToDo: PosType must be dynamic, get it from vm options
        init(POSType.BULLOCH);
        try {
            survey = new Survey(surveyProp);
        } catch (Exception e) {
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
        if (enablePlacement){
            jsonObject = survey.createPlacement();
            assertEquals(jsonObject.get("status"),"Success","Failed to create placement for survey!");
            placementID = jsonObject.get("id").toString();
            wait(3000);
            jsonObject = survey.linkPlacement(surveyID,placementID);
            assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
        }
        //link placement to survey
        return survey;
    }
    @AfterClass
    public void tearDown(){
        survey.deletePlacement(placementID);
        survey.deleteSurvey(surveyID);
        dbHandler.executeDeleteQuery(QUERY);
        dbHandler.closeConnection();
    }
}
