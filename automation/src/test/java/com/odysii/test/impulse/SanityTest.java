package com.odysii.test.impulse;

import com.odysii.api.pos.MessageGenerator;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.Helper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.assertEquals;

public class SanityTest {
    private final String IMPULSE_RUNNER_SCRIPT = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_impulse.exe\"";
    private final String ATB_RUNNER_SCRIPT = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_atb.exe\"";
    private final String CLOSE_IMPULSE_RUNNER_SCRIPT = "cmd /c start cmd.exe /K \"cd C:\\Program Files\\Odysii && run_close_impulse.exe\"";
    private final String ATB_LISTENER_URL = "http://localhost:8888/CashRegisterService/AddToBasket?getPLU";
    private final int WAIT = 4000;

    /**
     * Test- valid the correct an item PLU received from add to basket:
     * 1. Run impulse script
     * 2. Send to listener "Start transaction"
     * 3. Send to listener "Add item"
     * 4. Run add to basket script
     * 5. Get item PLU from add to basket listener service
     * 6. Run close impulse services script and assert equals expected PLU
     */
    @Ignore
    public void _001_passport_serial_AddToBasket() {
        Helper.init(POSType.PASSPORT_SERIAL);
        String expectedPLU = "A1000232";
        //Start Impulse
        runCmdCommand(IMPULSE_RUNNER_SCRIPT);
        wait(12000);
        MessageGenerator generator = new MessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|Begin Sale: Op #: 91|</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU (12) Coca Cola $11|</Body>");
        //add to basket
        runCmdCommand(ATB_RUNNER_SCRIPT);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(ATB_LISTENER_URL);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU ("+itemPLU+") auto item $1.79|</Body>");
        wait(WAIT);
        //End transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|End Sale: Op #: 91|</Body>");
        runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
    }

    @Ignore
    public void _002_passport_serial_AddToBasket_Fail() {
        wait(5000);
        String expectedPLU = "121212";
        //Start Impulse
        runCmdCommand(IMPULSE_RUNNER_SCRIPT);
        wait(12000);
        MessageGenerator generator = new MessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|Begin Sale: Op #: 91|</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU (12) Coca Cola $11|</Body>");
        //add to basket
        runCmdCommand(ATB_RUNNER_SCRIPT);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(ATB_LISTENER_URL);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU ("+itemPLU+") auto item $1.79|</Body>");
        wait(WAIT);
        //End transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|End Sale: Op #: 91|</Body>");
        runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
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
    public void _003_bulloch_AddToBasket() {
        //Helper.init(POSType.BULLOCH);
        String expectedPLU = "A1000232";
        //Start Impulse
        runCmdCommand(IMPULSE_RUNNER_SCRIPT);
        wait(12000);
        MessageGenerator generator = new MessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        //add to basket
        runCmdCommand(ATB_RUNNER_SCRIPT);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(ATB_LISTENER_URL);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>[C110] "+itemPLU.substring(1)+" ATB Item        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(WAIT);
        //End transaction
        //generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
    }
    @Test
    public void _004_bulloch_AddToBasketFail() {
        wait(5000);
        //Helper.init(POSType.BULLOCH);
        String expectedPLU = "1000232";
        //Start Impulse
        runCmdCommand(IMPULSE_RUNNER_SCRIPT);
        wait(12000);
        MessageGenerator generator = new MessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>[C000] NEWSALE  LANG=FR|/n</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>[C110] 0000000000037 MRSHMLOW SQ        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        //add to basket
        runCmdCommand(ATB_RUNNER_SCRIPT);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(ATB_LISTENER_URL);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>[C110] "+itemPLU.substring(1)+" ATB Item        QT=1 PR=1.79 AMT=1.79 STTL=1.79 DSC=0.00 TAX=0.23 TOTAL=2.02|/n</Body>");
        wait(WAIT);
        //End transaction
        //generator.doPostRequest("<Body>[C200] Sale TRANS=001326 TOTAL=3.12 CHNG=58.00 TAX=1.69|/n</Body>");
        runCmdCommand(CLOSE_IMPULSE_RUNNER_SCRIPT);
        assertEquals(itemPLU,expectedPLU);
    }

    private void runCmdCommand(String command){
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                wait(1000);
                Runtime.getRuntime().exec("taskkill /f /im cmd.exe") ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void wait(int miliSeconds){
        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
