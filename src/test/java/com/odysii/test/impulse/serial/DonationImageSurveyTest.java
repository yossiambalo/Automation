package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class DonationImageSurveyTest extends SurveyTestBase {

    @BeforeClass
    public void setUp(){
        setUp(SurveyType.DONATION_IMAGE,true);
    }

    //Test Cases: SUR-2-1,SUR-2-2,SUR-2-3,SUR-2-5
    @Test
    public void _001_validOptionNumber2TypeImage(){
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
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        DBHandler dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        dbHandler.closeConnection();
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _002_validOptionNumber3TypeImage(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body3");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey3dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }

    @Test
    public void _003_validOptionNumber4TypeImage(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body4");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey3dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
}
