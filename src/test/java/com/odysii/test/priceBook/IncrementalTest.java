package com.odysii.test.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.selenium.cloudMI.PriceBookLoaderType;
import com.odysii.selenium.cloudMI.PriceBookManager;
import com.odysii.selenium.cloudMI.ProjectPage;
import com.odysii.selenium.cloudMI.SignUpPage;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.odysii.general.fileUtil.FileHandler.getFile;
import static com.odysii.general.fileUtil.FileHandler.getFileName;
import static org.testng.Assert.*;

public class IncrementalTest extends ImpulseTestHelper{

    private String shardPath,localPath;
    private String newFileName;
    private boolean flag;
    private final String ITT_FILE_PERFIX = "ITT";
    private final String ILT_FILE_PERFIX = "ILT";
    private final String ROOT_NODE = "ItemMaintenance";
    private final String CHILD_NODE = "ITTDetail";
    private final String SIBLING_NODE = "ITTData";
    private final String UPDATE_NODE = "Description";
    private final int TIME_OUT = 30000;
    private long incrementNum;
    private long num;
    private String[]arr1;
    private WebDriver driver;

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
        wait(10000);
        /**
         * WebDriver Start
         */
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("http://cloudmiqa.tveez.local/projects/76");
        SignUpPage signUpPage = new SignUpPage(driver);
        signUpPage.enterCredentials("yossi.ambalo@odysii.com","Jt1Z1xwS");
        ProjectPage projectPage = signUpPage.submit();
        PriceBookManager priceBookManager = projectPage.getPriceBook();
        priceBookManager.setPricebookLoaderType(PriceBookLoaderType.INC);
        wait(5000);
        driver.quit();
        /**
         * WebDriver End
         */
    }

    /**
     * Update shared ITT file and validate local ITT file updated too respectively
     * 1. Update shared ITT file
     * 2. Increment timestamp(name of ITT file)
     */
    @Test(priority = 1)
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
    @Test(priority = 2,dependsOnMethods = {"_001_validILTLocalFileUpdatedRespectivelyIncrementTimestamp"})
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
    @Test(priority = 3)
    public void _003_validDeletedDataUpdatedRespectively(){
        boolean flag = XmlManager.replaceNodeAttribute(new File(newFileName),"RecordAction",0,"type","delete");
       assertTrue(flag,"Failed to replace Node attribute!");
        flag = XmlManager.deleteNode(new File(newFileName),"Description",0);
        assertTrue(flag,"Failed to delete Node!");
        incrementNum = incrementNum+2;
        String fileName = arr1[0]+ ITT_FILE_PERFIX +incrementNum+".xml";
        FileHandler.renameFile(new File(newFileName),fileName);
        wait(TIME_OUT);
        flag = XmlManager.isNodeExist(new File(localPath+File.separator+ ITT_FILE_PERFIX+incrementNum +".xml"),ROOT_NODE,CHILD_NODE,SIBLING_NODE,UPDATE_NODE);
        assertFalse(flag);
    }

    /**
     * File name not configured in cloudMI
     */
    @Test(priority = 4)
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
    @Test(priority = 5)
    public void _005_validFileWithFormatNotAddedToLocalWhileImpulseInit(){
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

    /**
     * Update multiple files in shared of same type(ITT) and valid local was synchronized
     * respectively- while impulse is running
     */
    @Test(dependsOnMethods = {"_005_validFileWithFormatNotAddedToLocalWhileImpulseInit"},priority = 6)
    public void _006_validLocalSyncMultipleUpdatedOfSharedFilesWhileImpulseRunning(){

        File file = getFile(shardPath,ITT_FILE_PERFIX);
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ITT_FILE_PERFIX);
        long copy1,copy2;
        copy1 = Long.parseLong(arr[1])+2;
        copy2 = copy1+2;
        String newFile1 = shardPath+"\\"+ITT_FILE_PERFIX+copy1+".xml";
        String newFile2 = shardPath+"\\"+ITT_FILE_PERFIX+copy2+".xml";
        FileHandler.copyFile(file.toString(),newFile1,true);
        FileHandler.copyFile(newFile1,newFile2,true);
        FileHandler.copyFile(shardPath+"\\backup\\item1.xml",newFile1,true);
        FileHandler.copyFile(shardPath+"\\backup\\item2.xml",newFile2,true);
        wait(20000);
        int size = XmlManager.getSizeOfNode(getFile(localPath,ITT_FILE_PERFIX),"ITTDetail");
        FileHandler.deleteFile(newFile1);
        FileHandler.deleteFile(file.toString());
        assertEquals(size,3,"Update multiple files in shared of same type(ITT) wasn't synchronized in local!");
    }
    /**
     * Update files in shared of same type(ITT) and valid local was synchronized
     * respectively with latest file- while impulse is init!
     */
    @Test(priority = 7)
    public void _007_validLocalUpdatedTheLatestSharedFilesWhileImpulseInit(){

        runCmdCommand(closeImpulseRunnerScript);
        boolean flag = FileHandler.copyFile(localPath+"\\backup\\ITTIncCopy.xml",getFile(localPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        wait(2000);
        File file = getFile(shardPath,ITT_FILE_PERFIX);
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ITT_FILE_PERFIX);
        long copy1,copy2;
        copy1 = Long.parseLong(arr[1])+2;
        copy2 = copy1+2;
        String newFile1 = shardPath+"\\"+ITT_FILE_PERFIX+copy1+".xml";
        String newFile2 = shardPath+"\\"+ITT_FILE_PERFIX+copy2+".xml";
        FileHandler.copyFile(file.toString(),newFile1,true);
        FileHandler.copyFile(newFile1,newFile2,true);
        FileHandler.copyFile(shardPath+"\\backup\\item1.xml",newFile1,true);
        FileHandler.copyFile(shardPath+"\\backup\\item2.xml",newFile2,true);
        runCmdCommand(impulseRunnerScript);
        wait(15000);
        int size = XmlManager.getSizeOfNode(getFile(localPath,ITT_FILE_PERFIX),"ITTDetail");
        FileHandler.deleteFile(newFile1);
        FileHandler.deleteFile(file.toString());
        assertEquals(size,3,"Update latest file of shared wasn't synchronized in local!");
    }
    @Test(priority =8)
    public void _008_validAllFileOfSharedUpdatedWhenTableActionSetToInit(){
        File file = getFile(shardPath,ITT_FILE_PERFIX);
        runCmdCommand(impulseRunnerScript);
        wait(2000);
        FileHandler.copyFile(shardPath+"\\backup\\ITTAll.xml",file.toString(),true);
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ITT_FILE_PERFIX);
        long copy1 = Long.parseLong(arr[1])+2;
        FileHandler.renameFile(file,shardPath+"\\ITT"+copy1+".xml");
        wait(20000);
        int size = XmlManager.getSizeOfNode(getFile(localPath,ITT_FILE_PERFIX),"ITTDetail");
        assertEquals(size,199);
    }
    @Test(priority = 9)
    public void _009_validLocalDeletedFileItRetakingWhileImpulseRunning(){
        File file = getFile(localPath,ITT_FILE_PERFIX);
        boolean res = FileHandler.deleteFile(file.toString());
        assertTrue(res,"Failed to delete file!");
        wait(20000);
        file = getFile(localPath,ITT_FILE_PERFIX);
        assertNotNull(file,"Failed to retaking file from shared!");
    }
    @Test(priority = 10)
    public void _010_validItemNotDuplicatedInLocal(){
        boolean flag = FileHandler.copyFile(localPath+"\\backup\\ITTIncCopy.xml",getFile(localPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        flag = FileHandler.copyFile(shardPath+"\\backup\\ITTIncCopy.xml",getFile(shardPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        flag = FileHandler.copyFile(shardPath+"\\backup\\ITTIncDuplicate.xml",getFile(shardPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        File file = getFile(shardPath,ITT_FILE_PERFIX);
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ITT_FILE_PERFIX);
        long copy1 = Long.parseLong(arr[1])+2;
        FileHandler.renameFile(file,shardPath+"\\ITT"+copy1+".xml");
        wait(20000);
        int size = XmlManager.getSizeOfNode(getFile(localPath,ITT_FILE_PERFIX),"ITTDetail");
        assertEquals(size,1);
    }
    @Test(priority = 11)
    public void _011_validItemDeletedInLocalWhenRecordActionDelete(){
        File file = getFile(shardPath,ILT_FILE_PERFIX);
        //Expected size
        int originalSize = XmlManager.getSizeOfNode(file,"ILTDetail");
        boolean flag = XmlManager.replaceNodeAttribute(file,"RecordAction",1,"type","delete");
        assertTrue(flag,"Failed to set node attribute to delete!");
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ILT_FILE_PERFIX);
        long copy1 = Long.parseLong(arr[1])+2;
        String newFileName = shardPath+"\\ILT"+copy1+".xml";
        FileHandler.renameFile(file,newFileName);
        wait(20000);
        int actualSize = XmlManager.getSizeOfNode(getFile(localPath,ILT_FILE_PERFIX),"ILTDetail");
        //Return the original value of node attribute
        flag = XmlManager.replaceNodeAttribute(new File(newFileName),"RecordAction",1,"type","addchange");
        assertTrue(flag,"Failed to set node attribute to addchange!");
        assertEquals(actualSize,originalSize-1);
    }
    @Test(priority = 12)
    public void _012_validItemWithoutRecordActionAddedToLocal() {
        File file = getFile(shardPath,ITT_FILE_PERFIX);
        boolean flag = FileHandler.copyFile(shardPath+"\\backup\\twoItems.xml",file.toString(),true);
        assertTrue(flag,"Failed to copy file!");
        int originalSize = XmlManager.getSizeOfNode(file,"RecordAction");
        XmlManager.deleteNode(file,"RecordAction",1);
        String fileStr = getFileName(file.toString());
        String[]arr = fileStr.split(ITT_FILE_PERFIX);
        long copy1 = Long.parseLong(arr[1])+2;
        String newFileName = shardPath+"\\ITT"+copy1+".xml";
        FileHandler.renameFile(file,newFileName);
        wait(20000);
        file = getFile(localPath,ITT_FILE_PERFIX);
        int actualSize = XmlManager.getSizeOfNode(file,"RecordAction");
        assertEquals(actualSize,originalSize);

    }

    /**
     * Verify same item with selling unit 2 not override local item with selling unit 1, should add it as new one
     */
    @Test(priority = 13)
    public void _013_validSameItemWithDifferentSellingUnitNotOverridden() {
        File file = getFile(localPath,ITT_FILE_PERFIX);
        boolean flag = FileHandler.copyFile(shardPath+"\\backup\\item2.xml",file.toString(),true);
        assertTrue(flag, "Failed to copy file!");
        file = getFile(shardPath,ITT_FILE_PERFIX);
        flag = FileHandler.copyFile(shardPath+"\\backup\\same_item_diff_unit.xml",file.toString(),true);
        assertTrue(flag);
        String fileName = getFileName(file.toString());
        String[]arr = fileName.split(ITT_FILE_PERFIX);
        String newFileName = shardPath+"\\ITT"+Long.parseLong(arr[1])+2+".xml";
        FileHandler.renameFile(file,newFileName);
        wait(1000);
        int originalSize = XmlManager.getSizeOfNode(new File(newFileName),"ITTDetail");
        wait(20000);
        file = getFile(localPath,ITT_FILE_PERFIX);
        int actualSize = XmlManager.getSizeOfNode(file,"ITTDetail");
        assertEquals(actualSize,originalSize);
    }
    @Test(priority = 14)
    public void _014_validITTFileWithUnderScoreUpdatedToLocal(){
        //delete file from local
        boolean res = FileHandler.deleteFile(getFile(localPath,ITT_FILE_PERFIX).toString());
        assertTrue(res,"Failed to delete file!");
        //get ITT  file from shared
        File file = getFile(shardPath,ITT_FILE_PERFIX);
        assertNotNull(file,"File not found!");
        FileHandler.renameFile(file,shardPath+"\\ITT_360217.xml");
        wait(TIME_OUT);
        //Check file updated to local with underscore
        String expectedFileName = getFileName(getFile(shardPath,ITT_FILE_PERFIX).toString());
        String actualFileName = getFileName(getFile(localPath,ITT_FILE_PERFIX).toString());
        FileHandler.renameFile(new File(shardPath+"\\ITT_360217.xml"),file.toString());
        assertEquals(actualFileName,expectedFileName);
    }
    @Test(priority = 15)
    public void _015_validFileContainsOnlyDeleteItemsNotAddedToLocal(){
         runCmdCommand(closeImpulseRunnerScript);
         wait(1000);
         //delete file from local
         boolean res = FileHandler.deleteFile(getFile(localPath,ITT_FILE_PERFIX).toString());
         assertTrue(res,"Failed to delete file!");
         boolean flag = FileHandler.copyFile(shardPath+"\\backup\\delete_items.xml",getFile(shardPath,ITT_FILE_PERFIX).toString(),true);
         assertTrue(flag,"Failed to delete file!");
         int originalSize = XmlManager.getSizeOfNode(getFile(shardPath,ITT_FILE_PERFIX),"ITTDetail");
         runCmdCommand(impulseRunnerScript);
         wait(TIME_OUT);
         int actualSize = XmlManager.getSizeOfNode(getFile(localPath,ITT_FILE_PERFIX),"ITTDetail");
         assertEquals(actualSize,originalSize -1,"Item type delete should not add to local!");
    }
    @AfterTest
    public void tearDown(){
        runCmdCommand(closeImpulseRunnerScript);
    }
}
