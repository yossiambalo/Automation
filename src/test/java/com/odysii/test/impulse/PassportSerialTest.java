package com.odysii.test.impulse;

import com.odysii.api.pos.SerialMessageGenerator;
import com.odysii.general.POSType;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PassportSerialTest extends ImpulseTestHelper {
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
    @Test
    public void _001_passport_serial_AddToBasket() {
        init(POSType.PASSPORT_SERIAL);
        String expectedPLU = "A1000232";
        //Start Impulse
        runCmdCommand(impluseRunnerScript);
        wait(12000);
        SerialMessageGenerator generator = new SerialMessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|Begin Sale: Op #: 91|</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU (12) Coca Cola $11|</Body>");
        //add to basket
        runCmdCommand(atbRunnerScript);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(atbListenerUrl);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU ("+itemPLU+") auto item $1.79|</Body>");
        wait(WAIT);
        //End transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|End Sale: Op #: 91|</Body>");
        runCmdCommand(closeImpluseRunnerScript);
        assertEquals(itemPLU,expectedPLU);
    }

    @Ignore
    public void _002_passport_serial_AddToBasket_Fail() {
        wait(5000);
        String expectedPLU = "121212";
        //Start Impulse
        runCmdCommand(impluseRunnerScript);
        wait(12000);
        SerialMessageGenerator generator = new SerialMessageGenerator();
        //Start transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|Begin Sale: Op #: 91|</Body>");
        wait(WAIT);
        //Add item
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU (12) Coca Cola $11|</Body>");
        //add to basket
        runCmdCommand(atbRunnerScript);
        wait(WAIT);
        //Get item PLU from ATBListener service
        String itemPLU = generator.doGetRequest(atbListenerUrl);
        wait(WAIT);
        //Add the item given from ATBListener service
        generator.doPostRequest("<Body>1|" + getDate() + "|6004|91|Added Item: PLU ("+itemPLU+") auto item $1.79|</Body>");
        wait(WAIT);
        //End transaction
        generator.doPostRequest("<Body>1|"+getDate()+"|6004|91|End Sale: Op #: 91|</Body>");
        runCmdCommand(closeImpluseRunnerScript);
        assertEquals(itemPLU,expectedPLU);
    }
}
