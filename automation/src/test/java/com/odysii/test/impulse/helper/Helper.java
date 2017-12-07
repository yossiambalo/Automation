package com.odysii.test.impulse.helper;

import com.odysii.general.POSType;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;

public class Helper {

    public static void init(POSType posType){
        if (posType.equals(POSType.BULLOCH)){
            FileHandler.deleteFile("C:\\Program Files\\Odysii\\PassportSerial.PosIntegration.dll");
            FileHandler.copyFile("\\\\Orion\\Public\\SysQA\\Odysii\\Odysii Installers\\Solutions\\C-Store 4\\QA\\POSIntegrations\\CStore4 v4.0.91 Integrations\\Bulloch.POSIntegration.dll","C:\\Program Files\\Odysii\\Bulloch.POSIntegration.dll");
            XmlManager.updateNode("C:\\Program Files\\Odysii\\Odysii.CNC.Manager.exe.config","Config","ChannelID","8766");
        }else {
            FileHandler.deleteFile("C:\\Program Files\\Odysii\\Bulloch.POSIntegration.dll");
            FileHandler.copyFile("\\\\Orion\\Public\\SysQA\\Odysii\\Odysii Installers\\Solutions\\C-Store 4\\QA\\POSIntegrations\\CStore4 v4.0.91 Integrations\\PassportSerial.PosIntegration.dll","C:\\Program Files\\Odysii\\PassportSerial.PosIntegration.dll");
            XmlManager.updateNode("C:\\Program Files\\Odysii\\Odysii.CNC.Manager.exe.config","Config","ChannelID","8765");
        }
    }
}
