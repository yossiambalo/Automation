package com.odysii.test.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

public class FunctionTest {
    protected WebDriver driver;

   @BeforeClass
    public void setUp(){
       System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
       driver = new ChromeDriver();
       driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
       driver.manage().window().maximize();
   }
    @AfterTest
    public void cleanUp(){
        driver.manage().deleteAllCookies();
    }
    @AfterClass
    public void tearDown() {
        driver.close();
    }
}
