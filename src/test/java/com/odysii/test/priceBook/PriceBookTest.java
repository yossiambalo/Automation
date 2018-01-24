package com.odysii.test.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class PriceBookTest extends ImpulseTestHelper{

    private boolean flag;
    //Increment timestamp
    @Test
    public void _001_validILTLocalFileUpdatedAccordinglyWhileImpulseRunning(){
        init(POSType.PASSPORT_SERIAL);
        flag = true;
        //Run impulse
        runCmdCommand(impulseRunnerScript);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        String localPath = properties.getProperty("local_pricebook_path");
        String shardPath = properties.getProperty("shard_pricebook_path");
        String filePerifix = "ILT";
        String wrapperNode = "ILTDetail";
        String childNode = "ItemListID";
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(filePerifix)){
                XmlManager.updateNode(file,wrapperNode,childNode,ittItemListIDValue);
                String[]arr1 = fileStr.split(filePerifix);
                String[]arr2 = arr1[1].split("\\.");
                long num = Long.parseLong(arr2[0]);
                System.out.println(num);
                long incrementNum = num+2;
                String newFileName = arr1[0]+filePerifix+incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(60000);
                String res = XmlManager.getValueOfNode(new File(localPath+"\\"+filePerifix+".xml"),wrapperNode,childNode);
                assertEquals(res,ittItemListIDValue);
            }
        }
    }
    //Discernment timestamp
    @Test
    public void _002_validILTLocalFileNotUpdatedAccordinglyWhileImpulseRunning(){
        if (!flag) {
            init(POSType.PASSPORT_SERIAL);
            //Run impulse
            runCmdCommand(impulseRunnerScript);
        }
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        String localPath = properties.getProperty("local_pricebook_path");
        String shardPath = properties.getProperty("shard_pricebook_path");
        String filePerifix = "ILT";
        String wrapperNode = "ILTDetail";
        String childNode = "ItemListID";
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(filePerifix)){
                XmlManager.updateNode(file,wrapperNode,childNode,ittItemListIDValue);
                String[]arr1 = fileStr.split(filePerifix);
                String[]arr2 = arr1[1].split("\\.");
                long num = Long.parseLong(arr2[0]);
                System.out.println(num);
                long incrementNum = num-2;
                String newFileName = arr1[0]+filePerifix+incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(60000);
                String res = XmlManager.getValueOfNode(new File(localPath+"\\"+filePerifix+".xml"),wrapperNode,childNode);
                assertNotEquals(res,ittItemListIDValue);
            }
        }
    }
}
