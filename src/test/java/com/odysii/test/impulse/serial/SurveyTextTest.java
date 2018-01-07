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

public class SurveyTextTest extends SurveyTestBase {

    @BeforeClass
    public void setUp(){
       setUp(SurveyType.SURVEY_TEXT,true);
    }

    //Test Cases: SUR-2-1,SUR-2-2,SUR-2-3,SUR-2-5
    @Test
    public void _001_validAnswerNumber2(){
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
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        DBHandler dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        dbHandler.closeConnection();
        assertEquals(actual,surveyOptionID);
    }
    //Test Cases: SUR-2-4
    @Test
    public void _002_validAnswerNumber3(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body3");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey3dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }

    //Test Cases: SUR-2-6
    @Test
    public void _003_validAnswerNumber4(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body4");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey3dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _004_validAnswerNumber5(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body5");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey4dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _005_validAnswerNumber6(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body6");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey4dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _006_validAnswerNumber7(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body7");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey4dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _007_validAnswerNumber8(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body8");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey4dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
    @Test
    public void _008_validAnswerNumber9(){

        List<String> options = new ArrayList<>();
        options.add("survey_option_body9");
        //create options for survey
        jsonObject = survey.addOption(surveyID,options);
        assertEquals(jsonObject.get("status"),"Success","Failed to create option to survey!");
        surveyOptionID = jsonObject.get("id").toString();
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
        runCmdCommand(survey4dOption);
        String query = "SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='"+surveyOptionID+"'";
        dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery(query,6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 20)){
            wait(4000);
            actual = dbHandler.executeSelectQuery(query,6);
            timeOut++;
        }
        assertEquals(actual,surveyOptionID);
    }
}
