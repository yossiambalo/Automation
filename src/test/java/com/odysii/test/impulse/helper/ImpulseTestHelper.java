package com.odysii.test.impulse.helper;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.pos.BullochSerial;
import com.odysii.pos.Customer;
import com.odysii.pos.PassportSerial;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ImpulseTestHelper {

    protected WebDriver driver;
    protected static String atbListenerUrl, impulseRunnerScript,atbRunnerScript, closeImpulseRunnerScript,
            surveyRunnerScript,survey3dOption,survey4dOption,impulseDeliveryStationUrl;
    protected static Customer customer;

    public static void init(POSType posType){
        String cncConfigFile,bullochIntDll,passportIntDll;

        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("impulseTestHelper.properties");
        atbListenerUrl = properties.getProperty("atb_listener_url");
        impulseRunnerScript = properties.getProperty("impulse_runner_script");
        atbRunnerScript = properties.getProperty("atb_runner_script");
        closeImpulseRunnerScript = properties.getProperty("close_impulse_runner_script");
        surveyRunnerScript = properties.getProperty("survey_runner_script");
        survey3dOption = properties.getProperty("survey_3d_option");
        survey4dOption = properties.getProperty("survey_4d_option");
        impulseDeliveryStationUrl = properties.getProperty("impulse_delivery_station_url");
        cncConfigFile = properties.getProperty("cnc_config_path");
        bullochIntDll = properties.getProperty("bulloch_pos_int_dll_path");
        passportIntDll = properties.getProperty("passport_pos_int_dll_path");

        if (posType.equals(POSType.BULLOCH)){
            customer = new BullochSerial();
            customer.init();
            if (FileHandler.isFileExist(passportIntDll)) {
                FileHandler.deleteFile(passportIntDll);
                FileHandler.copyFile(properties.getProperty("bulloch_pos_int_dll_source_path"), bullochIntDll,true);
                XmlManager.updateNodeContent(new File(cncConfigFile), "Config", "ChannelID", properties.getProperty("bulloch_chanel_id"));
            }
        }else {
            customer = new PassportSerial();
            customer.init();
            if (FileHandler.isFileExist(bullochIntDll)){
                FileHandler.deleteFile(bullochIntDll);
                FileHandler.copyFile(properties.getProperty("passport_pos_int_dll_source_path"),passportIntDll,true);
            }
            XmlManager.updateNodeContent(new File(cncConfigFile),"Config","ChannelID",properties.getProperty("passport_chanel_id"));
            XmlManager.updateNodeContent(new File(cncConfigFile),"Config","ProjectID",properties.getProperty("passport_project_id"));
            XmlManager.updateNodeContent(new File(cncConfigFile),"Config","CnCService",properties.getProperty("passport_CnCService"));
            XmlManager.updateNodeContent(new File(cncConfigFile),"Config","ProjectToken",properties.getProperty("passport_project_token"));
        }
    }
    protected void runCmdCommand(String command){
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
    protected void wait(int miliSeconds){
        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @AfterClass
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
        driver.quit();
    }
}
