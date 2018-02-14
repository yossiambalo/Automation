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
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class FullUpdateTest extends ImpulseTestHelper {

    private WebDriver driver;
    private String shardPath,localPath;
    private final String ITT_FILE_PERFIX = "ITT";
    private final String ILT_FILE_PERFIX = "ILT";

    @BeforeClass
    public void setUp(){
        init(POSType.PASSPORT_SERIAL);
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        /**
         * WebDriver Start
         */
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("http://cloudmiqa.tveez.local/projects/51");
        SignUpPage signUpPage = new SignUpPage(driver);
        signUpPage.enterCredentials("yossi.ambalo@odysii.com","Jt1Z1xwS");
        ProjectPage projectPage = signUpPage.submit();
        PriceBookManager priceBookManager = projectPage.getPriceBook();
        priceBookManager.setPricebookLoaderType(PriceBookLoaderType.FULL);
        driver.quit();
        /**
         * WebDriver End
         */
        runCmdCommand(impulseRunnerScript);
        wait(15000);
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("price_book.properties");
        localPath = properties.getProperty("local_pricebook_path");
        shardPath = properties.getProperty("shard_pricebook_path");
    }
    @AfterClass
    public void tearDown() {
        runCmdCommand(closeImpulseRunnerScript);
    }
    @Test
    public void _001_validPriceBookUpdateWhileImpulseRunning(){
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
        assertEquals(Long.parseLong(arr2[1]),changeFileName);
    }
    @Test
    public void _002_validPriceBookUpdateWhileImpulseInit(){
        runCmdCommand(closeImpulseRunnerScript);
        File originalFile = getFile(localPath,ITT_FILE_PERFIX);
        Boolean res = FileHandler.deleteFile(originalFile.toString());
        assertTrue(res,"Failed to delete file!");
        runCmdCommand(impulseRunnerScript);
        wait(10000);
        File actualFile = getFile(localPath,ITT_FILE_PERFIX);
        assertEquals(actualFile,originalFile);
    }
}
