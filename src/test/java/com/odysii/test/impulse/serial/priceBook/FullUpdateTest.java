package com.odysii.test.impulse.serial.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.general.fileUtil.XmlManager;
import com.odysii.selenium.cloudMI.PriceBookLoaderType;
import com.odysii.selenium.cloudMI.PriceBookManager;
import com.odysii.selenium.cloudMI.ProjectPage;
import com.odysii.selenium.cloudMI.SignUpPage;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.odysii.general.fileUtil.FileHandler.getFileByType;
import static com.odysii.general.fileUtil.FileHandler.getFileName;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertEquals;

public class FullUpdateTest extends ImpulseTestHelper {

    private String shardPath,localPath,priceBookHelper;
    private String newFileName;
    private boolean flag;
    private final String ITT_FILE_PERFIX = "ITT";
    private final String ILT_FILE_PERFIX = "ILT";
    private final String ROOT_NODE = "ItemMaintenance";
    private final String CHILD_NODE = "ITTDetail";
    private final String SIBLING_NODE = "ITTData";
    private final String UPDATE_NODE = "Description";
    private long incrementNum;
    private long num;
    private String[]arr1;
    private WebDriver driver;
    private final int TIME_OUT = 15000;

    @BeforeClass
    public void setUp(){
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
        priceBookManager.setPricebookLoaderType(PriceBookLoaderType.FULL);
        wait(5000);
        driver.quit();
        /**
         * WebDriver End
         */
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
        priceBookHelper = properties.getProperty("price_book_helper");
        FileHandler.cleanDirectory(localPath);
        FileHandler.copyDir(System.getProperty("sourceDir"),localPath);
        FileHandler.cleanDirectory(shardPath);
        FileHandler.copyDir(System.getProperty("sourceDir"),shardPath);
        init(POSType.PASSPORT_SERIAL);
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        boolean flag = FileHandler.copyFile(priceBookHelper+"\\ITTIncCopy.xml", getFileByType(localPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        flag = FileHandler.copyFile(priceBookHelper+"\\ITTIncCopy.xml", getFileByType(shardPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        wait(10000);
    }
    @AfterClass
    public void tearDown() {
        runCmdCommand(closeImpulseRunnerScript);
    }

    @Test(priority = 16)
    public void _001_validLocalDeletedFileItRetakingWhileImpulseRunning(){
        File file = getFileByType(localPath,ITT_FILE_PERFIX);
        boolean res = FileHandler.deleteFile(file.toString());
        assertTrue(res,"Failed to delete file!");
        wait(TIME_OUT+5000);
        file = getFileByType(localPath,ITT_FILE_PERFIX);
        assertNotNull(file,"Failed to retaking file from shared!");
    }
    @Test(priority = 17)
    public void _002_validLocalFileUpdateWhileImpulseRunning(){
        File sharedFile = getFileByType(shardPath,ITT_FILE_PERFIX);
        String fileName = getFileName(sharedFile.toString());
        String[]arr1 = fileName.split(ITT_FILE_PERFIX);
        long changeFileName = Long.parseLong(arr1[1])+2;
        String newFileName = shardPath+"\\ITT"+changeFileName+".xml";
        FileHandler.renameFile(sharedFile,newFileName);
        wait(20000);
        File localdFile = getFileByType(localPath,ITT_FILE_PERFIX);
        String fileName2 = getFileName(localdFile.toString());
        String[]arr2 = fileName2.split(ITT_FILE_PERFIX);
        assertEquals(changeFileName,Long.parseLong(arr2[1]));
    }
    @Test(priority = 18)
    public void _003_validLocalDeletedFileUpdateWhileImpulseInit(){
        runCmdCommand(closeImpulseRunnerScript);
        wait(2000);
        File deleteFile = getFileByType(localPath,ITT_FILE_PERFIX);
        Boolean res = FileHandler.deleteFile(deleteFile.toString());
        assertTrue(res,"Failed to delete file!");
        String originalFile = getFileName(getFileByType(shardPath,ITT_FILE_PERFIX).toString());
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        String actualFile = getFileName(getFileByType(localPath,ITT_FILE_PERFIX).toString());
        assertEquals(actualFile,originalFile);
    }
    @Test(priority = 19)
    public void _004_validFileWithTxtFormatNotAddedToLocalWhileImpulseInit(){
        wait(5000);
        runCmdCommand(closeImpulseRunnerScript);
        String newFileName = "";
        File file = getFileByType(shardPath,ILT_FILE_PERFIX);
        assertNotNull(file,"File not found in: "+shardPath);
        String[] arr = file.toString().split("\\.");
        newFileName = arr[0]+".txt";
        FileHandler.renameFile(file,newFileName);
        File file2 = getFileByType(localPath,ILT_FILE_PERFIX);
        FileHandler.deleteFile(file2.toString());
        wait(3000);
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        File localFile = getFileByType(localPath,newFileName.split("\\\\")[2]);
        FileHandler.renameFile(new File(newFileName),file.toString());
        assertNull(localFile,"Invalid File found in: "+localPath);
    }
    /**
     * Update shared ITT file and validate local ITT file updated too respectively
     * 1. Update shared ITT file
     * 2. Increment timestamp(name of ITT file)
     */
    @Test(priority = 20)
    public void _005_validILTLocalFileUpdatedRespectivelyIncrementTimestamp(){
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
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
                Assert.assertEquals(res,ittItemListIDValue);
            }
        }
    }
    //@Ignore
    public void _006_validILTLocalFileNotUpdatedTimestampNotChanged(){
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File[]files = FileHandler.getFilesOfFolder(shardPath);
        for (File file : files){
            String fileStr = file.toString();
            if (fileStr.contains(ILT_FILE_PERFIX)){
                XmlManager.updateNodeContent(file,SIBLING_NODE,UPDATE_NODE,ittItemListIDValue);
                wait(TIME_OUT);
                String res = XmlManager.getValueOfLastNode(new File(localPath+File.separator+ ILT_FILE_PERFIX+incrementNum +".xml"),ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
                assertNotEquals(res,ittItemListIDValue);
            }
        }
    }
    @Test(priority = 21)
    public void _007_validITTFileWithUnderScoreUpdatedToLocal(){
        //delete file from local
        boolean res = FileHandler.deleteFile(getFileByType(localPath,ITT_FILE_PERFIX).toString());
        assertTrue(res,"Failed to delete file!");
        //get ITT  file from shared
        File file = getFileByType(shardPath,ITT_FILE_PERFIX);
        assertNotNull(file,"File not found!");
        FileHandler.renameFile(file,shardPath+"\\ITT_360217.xml");
        wait(TIME_OUT+5000);
        //Check file updated to local with underscore
        String expectedFileName = getFileName(getFileByType(shardPath,ITT_FILE_PERFIX).toString());
        String actualFileName = getFileName(getFileByType(localPath,ITT_FILE_PERFIX).toString());
        FileHandler.renameFile(new File(shardPath+"\\ITT_360217.xml"),file.toString());
        assertEquals(actualFileName,expectedFileName);
    }
    /**
     * File name not configured in cloudMI
     */
    @Test(priority = 22)
    public void _008_validFileWithInvalidNamesNotAddedToLocalWhileImpulseInit(){
        String newFileName = "";
        File file = getFileByType(shardPath,ILT_FILE_PERFIX);
        assertNotNull(file,"File not found in: "+shardPath);
        newFileName = shardPath+"\\YOS36020170313153531.xml";
        FileHandler.renameFile(file,newFileName);
        wait(1000);
        runCmdCommand(impulseRunnerScript);
        wait(10000);
        File localFile = getFileByType(localPath,"YOS");
        FileHandler.renameFile(new File(newFileName),file.toString());
        assertNull(localFile,"Invalid File found in: "+localPath);
    }
    @Test(priority = 23)
    public void _009_validITTLocalFileNotUpdatedTimestampNotChanged(){
        wait(5000);
        boolean flag = FileHandler.copyFile(priceBookHelper+"\\ITTIncCopy.xml", getFileByType(shardPath,ITT_FILE_PERFIX).toString(),true);
        assertTrue(flag,"Failed to copy file!");
        Random rand = new Random();
        int  n = rand.nextInt(20000) + 100;
        System.out.println("Random Number: "+n);
        String ittItemListIDValue = String.valueOf(n);
        File file = getFileByType(shardPath,ITT_FILE_PERFIX);
        XmlManager.updateNodeContent(file,SIBLING_NODE,UPDATE_NODE,ittItemListIDValue);
        wait(TIME_OUT+2000);
        String res = XmlManager.getValueOfLastNode(getFileByType(localPath,ITT_FILE_PERFIX),ROOT_NODE,CHILD_NODE, SIBLING_NODE,UPDATE_NODE);
        assertNotEquals(res,ittItemListIDValue);
    }
}
