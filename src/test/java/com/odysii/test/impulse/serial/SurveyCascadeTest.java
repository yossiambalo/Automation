package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.survey.Survey;
import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class SurveyCascadeTest extends SurveyTestBase {

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
        wait(20000);
        generator = new SerialMessageGenerator("http://localhost:7007/OdysiiDeliveryStation/");
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(2000);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(2000);
        //finish transaction
        generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        wait(3000);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(1000);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_1"));
        wait(1000);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_2"));
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1634'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
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
        runCmdCommand(impulseRunnerScript);
        //Wait CNC client downloading the new survey instructions
        wait(20000);
        generator = new SerialMessageGenerator("http://localhost:7007/OdysiiDeliveryStation/");
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(2000);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(2000);
        //finish transaction
        generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        wait(3000);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(1000);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_1"));
        wait(1000);
        runCmdCommand(survey.getProperties().getProperty("cascade_option_num_2"));
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1634'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        dbHandler.closeConnection();
        survey.deletePlacement(placementID);
        assertEquals(actual,"1634");
    }
}
