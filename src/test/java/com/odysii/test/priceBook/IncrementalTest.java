package com.odysii.test.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import static org.testng.Assert.*;

public class IncrementalTest extends ImpulseTestHelper{

    private String shardPath,localPath;
    private String newFileName;
    private boolean flag;
    private final String ITT_FILE_PERFIX = "ITT";
    private final String ILT_FILE_PERFIX = "ILT";
    private final String ROOT_NODE = "ItemMaintenance";//"ITTData";
    private final String CHILD_NODE = "ITTDetail";//"Description";
    private final String SIBLING_NODE = "ITTData";
    private final String UPDATE_NODE = "Description";
    private final int TIME_OUT = 30000;
    private long incrementNum;
    private long num;
    private String[]arr1;


    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        String fileName = "";
        //Run impulse
        runCmdCommand(impulseRunnerScript);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        boolean flag = FileHandler.copyFile(localPath+"\\backup\\ITTIncCopy.xml",getFile(localPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        flag = FileHandler.copyFile(shardPath+"\\backup\\ITTIncCopy.xml",getFile(shardPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
    }

    /**
     * Update shared ITT file and validate local ITT file updated too respectively
     * 1. Update shared ITT file
     * 2. Increment timestamp(name of ITT file)
     */
    @Test
    public void _001_validILTLocalFileUpdatedRespectivelyIncrementTimestamp(){
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(ITT_FILE_PERFIX)){
                XmlManager.updateNodeContent(file, SIBLING_NODE, UPDATE_NODE,ittItemListIDValue);
                arr1 = fileStr.split(ITT_FILE_PERFIX);
                String[]arr2 = arr1[1].split("\\.");
                num = Long.parseLong(arr2[0]);
                System.out.println(num);
                incrementNum = num+2;
                newFileName = arr1[0]+ ITT_FILE_PERFIX +incrementNum+".xml";
                System.out.println(newFileName);
                FileHandler.renameFile(file,newFileName);
                wait(TIME_OUT);
                String res = XmlManager.getValueOfLastNode(new File(localPath+File.separator+ ITT_FILE_PERFIX+incrementNum +".xml"), ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
                assertEquals(res,ittItemListIDValue);
            }
        }
    }
    @Test
    public void _002_validITTLocalFileNotUpdatedTimestampNotChanged(){
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(ITT_FILE_PERFIX)){
                XmlManager.updateNodeContent(file,SIBLING_NODE,UPDATE_NODE,ittItemListIDValue);
                wait(TIME_OUT);
                String res = XmlManager.getValueOfLastNode(new File(localPath+File.separator+ ITT_FILE_PERFIX+incrementNum +".xml"),ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
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
        String fileName = arr1[0]+ ITT_FILE_PERFIX +incrementNum+".xml";
        FileHandler.renameFile(new File(newFileName),fileName);
        wait(TIME_OUT);
        flag = XmlManager.isNodeExist(new File(localPath+File.separator+ ITT_FILE_PERFIX+incrementNum +".xml"),ROOT_NODE,CHILD_NODE,SIBLING_NODE,UPDATE_NODE);
        assertFalse(flag);
    }
    @Test
    public void _004_validFileWithInvalidNamesNotAddedToLocalWhileImpulseInit(){
        runCmdCommand(closeImpulseRunnerScript);
        String newFileName = "";
        File file = getFile(shardPath,ILT_FILE_PERFIX);
        assertNotNull(file,"File not found in: "+shardPath);
        newFileName = shardPath+"\\YOS36020170313153531.xml";
        FileHandler.renameFile(file,newFileName);
        wait(1000);
        runCmdCommand(impulseRunnerScript);
        wait(10000);
        File localFile = getFile(localPath,"YOS");
        FileHandler.renameFile(new File(newFileName),file.toString());
        assertNull(localFile,"Invalid File found in: "+localPath);
    }
    @Test
    public void _005_validFileWithInvalidExtentionNotAddedToLocalWhileImpulseInit(){
        wait(5000);
        runCmdCommand(closeImpulseRunnerScript);
        String newFileName = "";
        File file = getFile(shardPath,ILT_FILE_PERFIX);
        assertNotNull(file,"File not found in: "+shardPath);
        String[] arr = file.toString().split("\\.");
        newFileName = arr[0]+".txt";
        FileHandler.renameFile(file,newFileName);
        File file2 = getFile(localPath,ILT_FILE_PERFIX);
        FileHandler.deleteFile(file2.toString());
        wait(1000);
        runCmdCommand(impulseRunnerScript);
        wait(10000);
        File localFile = getFile(localPath,newFileName.split("\\\\")[2]);
        FileHandler.renameFile(new File(newFileName),file.toString());
        assertNull(localFile,"Invalid File found in: "+localPath);
    }
    private File getFile(String folder,String type){
        File resFile = null;
        File[]files = FileHandler.getFilesOfFolder(folder);
        for (File file : files) {
            String fileStr = file.toString();
            if (fileStr.contains(type)) {
                resFile = file;
            }
        }
        return resFile;
    }

    @AfterTest
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
    }
}
