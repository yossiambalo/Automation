package com.odysii.test.impulse.serial.survey;

import com.odysii.api.cloudMI.survey.SurveyType;
import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.test.impulse.serial.survey.helper.SurveyTestBase;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TriviaTest extends SurveyTestBase {

    @BeforeClass
    public void setUp() {
        setUp(SurveyType.TRIVIA, true, "placement_targeted_body");
    }

    @Test
    public void _001_validATBWhenSurveyOptionSelected() {
        String expectedPLU = "A1000232";
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
        //execute survey
        runCmdCommand(surveyRunnerScript);
        wait(3000);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(atbListenerUrl);
        int counter = 0;
        while (StringUtils.isEmpty(itemPLU)&& counter < 5){
            itemPLU = generator.doGetRequest(atbListenerUrl);
            wait(1000);
            counter++;
        }
        assertEquals(itemPLU,expectedPLU);
    }
}
