package com.odysii.test.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import static org.testng.Assert.*;

public class IncrementalTest extends ImpulseTestHelper{

    private String shardPath,localPath;
    private String newFileName;
    private boolean flag;
    private final String FILE_PERFIX = "ITT";
    private final String ROOT_NODE = "ItemMaintenance";//"ITTData";
    private final String CHILD_NODE = "ITTDetail";//"Description";
    private final String SIBLING_NODE = "ITTData";
    private final String UPDATE_NODE = "Description";
    private final int TIME_OUT = 30000;
    private long incrementNum;
    private long num;
    private String[]arr1;
    //Increment timestamp
    @Test
    public void _001_validILTLocalFileUpdatedRespectivelyIncrementTimestamp(){
        init(POSType.PASSPORT_SERIAL);
        flag = true;
        //Run impulse
        runCmdCommand(impulseRunnerScript);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(FILE_PERFIX)){
                XmlManager.updateNodeContent(file, SIBLING_NODE, UPDATE_NODE,ittItemListIDValue);
                arr1 = fileStr.split(FILE_PERFIX);
                String[]arr2 = arr1[1].split("\\.");
                num = Long.parseLong(arr2[0]);
                System.out.println(num);
                incrementNum = num+2;
                newFileName = arr1[0]+ FILE_PERFIX +incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(TIME_OUT);
                String res = XmlManager.getValueOfLastNode(new File(localPath+"\\"+ FILE_PERFIX +".xml"), ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
                assertEquals(res,ittItemListIDValue);
            }
        }
    }
    //Discernment timestamp
    @Test
    public void _002_validILTLocalFileNotUpdatedTimestampNotChanged(){
        if (!flag) {
            init(POSType.PASSPORT_SERIAL);
            //Run impulse
            runCmdCommand(impulseRunnerScript);
        }
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        String localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(FILE_PERFIX)){
                XmlManager.updateNodeContent(file,SIBLING_NODE,UPDATE_NODE,ittItemListIDValue);
                wait(TIME_OUT);
                String res = XmlManager.getValueOfLastNode(new File(localPath+File.separator+FILE_PERFIX+".xml"),ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
                assertNotEquals(res,ittItemListIDValue);
            }
        }
    }
    @Test
    public void _003_validDeletedDataUpdatedRespectively(){
        boolean flag = XmlManager.replaceNodeAttribute(new File(newFileName),"RecordAction","type","delete");
       assertTrue(flag,"Failed to replace Node attribute!");
        flag = XmlManager.deleteNode(new File(newFileName),"Description");
        assertTrue(flag,"Failed to delete Node!");
        incrementNum = incrementNum+2;
        String fileName = arr1[0]+FILE_PERFIX+incrementNum+".xml";
        FileHandler.renameFile(new File(newFileName),fileName);
        wait(TIME_OUT);
        flag = FileHandler.copyFile("C:\\data\\backup\\ITTCopy.xml",fileName,true);
        assertTrue(flag,"Failed to copy file!");
        flag = XmlManager.isNodeExist(new File("C:\\pb\\yossi\\ITT.xml"),ROOT_NODE,CHILD_NODE,SIBLING_NODE,UPDATE_NODE);
        assertFalse(flag);
    }

    @AfterTest
    public void tearDown(){
        FileHandler.copyFile("C:\\pb\\yossi\\backup\\ITTCopy.xml","C:\\pb\\yossi\\ITT.xml",true);
        runCmdCommand(closeImpulseRunnerScript);
    }
}
