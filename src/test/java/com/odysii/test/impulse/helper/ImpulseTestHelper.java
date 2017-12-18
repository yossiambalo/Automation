package com.odysii.test.impulse.helper;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ImpulseTestHelper {

    protected static String atbListenerUrl,impluseRunnerScript,atbRunnerScript,closeImpluseRunnerScript,surveyRunnerScript;

    public static void init(POSType posType){
        String cncConfigFile,bullochIntDll,passportIntDll;

        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("impulseTestHelper.properties");
        atbListenerUrl = properties.getProperty("atb_listener_url");
        impluseRunnerScript = properties.getProperty("impulse_runner_script");
        atbRunnerScript = properties.getProperty("atb_runner_script");
        closeImpluseRunnerScript = properties.getProperty("close_impulse_runner_script");
        surveyRunnerScript = properties.getProperty("survey_runner_script");
        cncConfigFile = properties.getProperty("cnc_config_path");
        bullochIntDll = properties.getProperty("bulloch_pos_int_dll_path");
        passportIntDll = properties.getProperty("passport_pos_int_dll_path");

        if (posType.equals(POSType.BULLOCH)){
            FileHandler.deleteFile(passportIntDll);
            FileHandler.copyFile(properties.getProperty("bulloch_pos_int_dll_source_path"),bullochIntDll);
            XmlManager.updateNode(cncConfigFile,"Config","ChannelID",properties.getProperty("bulloch_chanel_id"));
        }else {
            FileHandler.deleteFile(bullochIntDll);
            FileHandler.copyFile(properties.getProperty("passport_pos_int_dll_source_path"),passportIntDll);
            XmlManager.updateNode(cncConfigFile,"Config","ChannelID",properties.getProperty("passport_chanel_id"));
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

}
