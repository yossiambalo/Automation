package com.odysii.test.priceBook;

import com.odysii.general.POSType;
import com.odysii.general.PropertyLoader;
import com.odysii.general.fileUtil.FileHandler;
import com.odysii.selenium.cloudMI.PriceBookLoaderType;
import com.odysii.selenium.cloudMI.PriceBookManager;
import com.odysii.selenium.cloudMI.ProjectPage;
import com.odysii.selenium.cloudMI.SignUpPage;
import com.odysii.test.impulse.helper.ImpulseTestHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.odysii.general.fileUtil.FileHandler.getFile;
import static com.odysii.general.fileUtil.FileHandler.getFileName;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class FullUpdateTest extends ImpulseTestHelper {

    private WebDriver driver;
    private String shardPath,localPath;
    private final String ITT_FILE_PERFIX = "ITT";
    private final String ILT_FILE_PERFIX = "ILT";
    private final int TIME_OUT = 15000;

    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
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
        wait(10000);
        driver.quit();
        /**
         * WebDriver End
         */
    }
    @AfterClass
    public void tearDown() {
        runCmdCommand(closeImpulseRunnerScript);
    }

    @Test
    public void _001_validLocalDeletedFileItRetakingWhileImpulseRunning(){
        File file = getFile(localPath,ITT_FILE_PERFIX);
        boolean res = FileHandler.deleteFile(file.toString());
        assertTrue(res,"Failed to delete file!");
        wait(TIME_OUT+5000);
        file = getFile(localPath,ITT_FILE_PERFIX);
        assertNotNull(file,"Failed to retaking file from shared!");
    }
    @Test
    public void _002_validLocalFileUpdateWhileImpulseRunning(){
        File sharedFile = getFile(shardPath,ITT_FILE_PERFIX);
        String fileName = getFileName(sharedFile.toString());
        String[]arr1 = fileName.split(ITT_FILE_PERFIX);
        long changeFileName = Long.parseLong(arr1[1])+2;
        String newFileName = shardPath+"\\ITT"+changeFileName+".xml";
        FileHandler.renameFile(sharedFile,newFileName);
        wait(20000);
        File localdFile = getFile(localPath,ITT_FILE_PERFIX);
        String fileName2 = getFileName(localdFile.toString());
        String[]arr2 = fileName2.split(ITT_FILE_PERFIX);
        assertEquals(changeFileName,Long.parseLong(arr2[1]));
    }
    @Test
    public void _003_validLocalDeletedFileUpdateWhileImpulseInit(){
        runCmdCommand(closeImpulseRunnerScript);
        File deleteFile = getFile(localPath,ITT_FILE_PERFIX);
        Boolean res = FileHandler.deleteFile(deleteFile.toString());
        assertTrue(res,"Failed to delete file!");
        File originalFile = getFile(localPath,ITT_FILE_PERFIX);
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        File actualFile = getFile(localPath,ITT_FILE_PERFIX);
        assertEquals(actualFile,originalFile);
    }
    @Test
    public void _004_validFileWithTxtFormatNotAddedToLocalWhileImpulseInit(){
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
        wait(3000);
        runCmdCommand(impulseRunnerScript);
        wait(TIME_OUT);
        File localFile = getFile(localPath,newFileName.split("\\\\")[2]);
        FileHandler.renameFile(new File(newFileName),file.toString());
        assertNull(localFile,"Invalid File found in: "+localPath);
    }
}
