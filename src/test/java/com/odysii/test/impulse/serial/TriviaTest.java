package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TriviaTest extends SurveyTestBase {

    @BeforeClass
    public void setUp() {
        setUp(SurveyType.TRIVIA, true, "placement_targeted_body");
    }

    @Test
    public void _001_validAnswerNumber2() {
        String expectedPLU = "A1000232";
        //Start Impulse
        runCmdCommand(impulseRunnerScript);
        //Wait CNC client downloading the new survey instructions
        wait(CNC_DOWNLOAD_WAIT);
        generator = new SerialMessageGenerator("http://localhost:7007/OdysiiDeliveryStation/");
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(WAIT);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(atbListenerUrl);
        assertEquals(itemPLU,expectedPLU);
    }
}
