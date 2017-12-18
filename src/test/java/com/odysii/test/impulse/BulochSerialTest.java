package com.odysii.test.impulse;

import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.db.DBHandler;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BulochSerialTest extends ImpulseTestHelper{
    private final int WAIT = 4000;

    SerialMessageGenerator generator;
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
        String expectedPLU = "A2525";
        //Start Impulse
        runCmdCommand(impluseRunnerScript);
        wait(12000);
        generator = new SerialMessageGenerator();
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
        //generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        //runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
    }
    @Test
    public void _002_Bulloch_Valid_Survey() {
        wait(5000);
        generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        wait(3000);
        runCmdCommand(surveyRunnerScript);
        //connect to DB and execute queries
        DBHandler dbHandler = new DBHandler("jdbc:sqlserver://10.28.76.71:1433;databaseName=DW_qa;user=sa;password=Gladiator01",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dbHandler.executeSelectQuery("SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where ChannelId='8766'");
        dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[SurveyJournal] where ChannelId='8766'");
        dbHandler.closeConnection();
    }
}
