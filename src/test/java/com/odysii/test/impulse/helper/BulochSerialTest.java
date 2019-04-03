package com.odysii.test.impulse.helper;

import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BulochSerialTest extends ImpulseTestHelper{
    private final int WAIT = 4000;

    SerialMessageGenerator generator;
    @BeforeClass
    public void setUp(){
        DBHandler dbHandler = new DBHandler();
        dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1633'");
    }
    /**
     * Test- valid the correct an item PLU received from add to basket:
     * 1. Run impulse script
     * 2. Send to listener "Start transaction"
     * 3. Send to listener "Add item"
     * 4. Run add to basket script
     * 5. Get item PLU from add to basket listener service
     * 6. Run close impulse services script and assert equals expected PLU
     */
    @Test
    public void _001_bulloch_AddToBasket() {
        init(POSType.BULLOCH);
        String expectedPLU = "A1000232";
        //Start Impulse
        runCmdCommand(impulseRunnerScript);
        wait(12000);
        generator = new SerialMessageGenerator("http://localhost:7007/OdysiiDeliveryStation/");
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        //add to basket
        runCmdCommand(atbRunnerScript);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(atbListenerUrl);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>[C110] "+itemPLU.substring(1)+" ATB Item        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(WAIT);
        //End transaction
        //generator.createSurvey("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        //runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
    }
    //@Ignore
    public void _002_Bulloch_Valid_Survey() {
        wait(5000);
        //finish transaction
        generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        wait(3000);
        //execute survey
        runCmdCommand(surveyRunnerScript);
        //connect to DB and execute queries
        DBHandler dbHandler = new DBHandler();
        String actual = dbHandler.executeSelectQuery("SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1633'",6);
        int timeOut = 0;
        while((StringUtils.isEmpty(actual) && timeOut < 10)){
            wait(4000);
            actual = dbHandler.executeSelectQuery("SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where OptionId='1633'",6);
            timeOut++;
        }
        dbHandler.closeConnection();
        assertEquals(actual,"1633");
    }
    //@AfterClass
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
    }
}
