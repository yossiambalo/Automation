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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class PriceBookTest extends ImpulseTestHelper{

    private String shardPath;
    private String newFileName;
    private boolean flag;
    //Increment timestamp
    @Ignore
    public void _001_inc_validILTLocalFileUpdatedRespectivelyWhileImpulseRunning(){
        init(POSType.PASSPORT_SERIAL);
        flag = true;
        //Run impulse
        runCmdCommand(impulseRunnerScript);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        String localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        String filePerifix = "ITT";
        String wrapperNode = "ITTData";
        String childNode = "Description";
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(filePerifix)){
                XmlManager.updateNodeContent(file,wrapperNode,childNode,ittItemListIDValue);
                String[]arr1 = fileStr.split(filePerifix);
                String[]arr2 = arr1[1].split("\\.");
                long num = Long.parseLong(arr2[0]);
                System.out.println(num);
                long incrementNum = num+2;
                newFileName = arr1[0]+filePerifix+incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(60000);
                String res = XmlManager.getValueOfNode(new File(localPath+"\\"+filePerifix+".xml"),wrapperNode,childNode);
                assertEquals(res,ittItemListIDValue);
            }
        }
    }
    //Discernment timestamp
    @Ignore
    public void _002_inc_validILTLocalFileNotUpdatedRespectivelyWhileImpulseRunning(){
        if (!flag) {
            init(POSType.PASSPORT_SERIAL);
            //Run impulse
            runCmdCommand(impulseRunnerScript);
        }
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        String localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        String filePerifix = "ITT";
        String wrapperNode = "ITTData";
        String childNode = "Description";
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(filePerifix)){
                XmlManager.updateNodeContent(file,wrapperNode,childNode,ittItemListIDValue);
                String[]arr1 = fileStr.split(filePerifix);
                String[]arr2 = arr1[1].split("\\.");
                long num = Long.parseLong(arr2[0]);
                System.out.println(num);
                long incrementNum = num-2;
                String newFileName = arr1[0]+filePerifix+incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(70000);
                String res = XmlManager.getValueOfNode(new File(localPath+File.separator+filePerifix+".xml"),wrapperNode,childNode);
                assertNotEquals(res,ittItemListIDValue);
            }
        }
    }
    @Test
    public void _003_inc_validDeletedDataUpdatedRespectively(){
        boolean flag = XmlManager.replaceNodeAttribute(new File("C:\\data\\ITT36020170313153543.xml"),"RecordAction","type","addchange","delete");
        if (flag){
            flag = XmlManager.deleteNode(new File(newFileName),"Description");
            if (flag){
                wait(60000);
            }

        }
    }

    @AfterTest
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
    }
}
