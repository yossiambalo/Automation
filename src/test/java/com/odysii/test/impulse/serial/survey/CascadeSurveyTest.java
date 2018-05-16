package com.odysii.test.impulse.serial.survey;

import com.odysii.api.cloudMI.survey.Survey;
import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.test.impulse.serial.survey.helper.SurveyTestBase;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CascadeSurveyTest extends SurveyTestBase {

    private Survey survey;
    @BeforeClass
    public void setUp(){
        survey = setUp(SurveyType.SURVEY_CASCADE,true);
    }

    @Test
    public void _001_moveUserToNextSurveyWithoutPlacement(){
        //Start Impulse
        runCmdCommand(impulseRunnerScript);
        //Wait CNC client downloading the new survey instructions
        wait(CNC_DOWNLOAD_WAIT);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(WAIT);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(WAIT);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        wait(WAIT);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(WAIT);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_1"));
        wait(WAIT);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_2"));
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1634'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        dbHandler.executeDeleteQuery(QUERY);
        dbHandler.closeConnection();
        assertEquals(actual,"1634");
    }
    @Test
    public void _002_moveUserToNextSurveyWithPlacement(){
        jsonObject = survey.createPlacement("placement_body2");
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for survey!");
        String placementID = jsonObject.get("id").toString();
        jsonObject = survey.linkPlacement("1266",placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
        jsonObject = survey.linkPlacement("1264",placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
        //Start Impulse
        //runCmdCommand(impulseRunnerScript);
        //Wait CNC client downloading the new survey instructions
        wait(CNC_DOWNLOAD_WAIT);
        generator = new SerialMessageGenerator(impulseDeliveryStationUrl);
        //Start transaction
        generator.doPostRequest(customer.getStartTransaction());
        wait(WAIT);
        //Add item
        generator.doPostRequest(customer.getAddItem());
        wait(WAIT);
        //finish transaction
        generator.doPostRequest(customer.getEndTransaction());
        wait(WAIT);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(WAIT);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_1"));
        wait(WAIT);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_2"));
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1634'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        survey.deletePlacement(placementID);
        assertEquals(actual,"1634");
    }
}
