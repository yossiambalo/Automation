package com.odysii.test.impulse.serial.survey;

import com.odysii.api.cloudMI.survey.Survey;
import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.test.impulse.serial.survey.helper.SurveyTestBase;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class SurveyImageTest extends SurveyTestBase {

    private Survey survey;
    @BeforeClass
    public void setUp(){
        survey = setUp(SurveyType.SURVEY_IMAGE,false);
    }

    @Test
    //Survey without placement should not be play in impulse
    public void _001_validSurveyWithoutPlacement(){
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
        assertNotEquals(actual,surveyOptionID);
    }
    //Test Cases: SUR-2-1,SUR-2-2,SUR-2-3,SUR-2-5
    @Test
    public void _002_validAnswerNumber2(){
        jsonObject = survey.createPlacement();
        assertEquals(jsonObject.get("status"),"Success","Failed to create placement for survey!");
        placementID = jsonObject.get("id").toString();
        wait(WAIT);
        jsonObject = survey.linkPlacement(surveyID,placementID);
        assertEquals(jsonObject.get("status"),"Success","Failed to link placement for survey!");
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
    //Test Cases: SUR-2-4
    @Test
    public void _003_validAnswerNumber3(){

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

    //Test Cases: SUR-2-6
    @Test
    public void _004_validAnswerNumber4(){

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
        runCmdCommand(survey.getProperties().getProperty("image_option_num_4"));
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
    public void _005_validAnswerNumber5(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body5");
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
        runCmdCommand(survey4dOption);
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
    public void _006_validAnswerNumber6(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body6");
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
        runCmdCommand(survey.getProperties().getProperty("image_option_num_6"));
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
    public void _007_validAnswerNumber7(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body7");
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
        runCmdCommand(survey.getProperties().getProperty("image_option_num_7"));
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
    public void _008_validAnswerNumber8(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body8");
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
        runCmdCommand(survey4dOption);
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
    //Survey is image type and following option will be without image
    //and options should not be display
    public void _009_validAnswerNumber9ShouldNotBeDisplayed(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body9");
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
        runCmdCommand(survey.getProperties().getProperty("image_option_num_9"));
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(WAIT);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertNotEquals(actual,surveyOptionID);
    }
}
