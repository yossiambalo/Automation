package com.odysii.test;

import com.odysii.general.fileUtil.XmlManager;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class TestNGSample {
    @Ignore
    public void testCloudMI() {
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        WebDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        chromeDriver.manage().window().maximize();
        chromeDriver.get("http://cloudmiqa.tveez.local/projects/51");
        chromeDriver.findElement(By.id("user_email")).sendKeys("yossi.ambalo@odysii.com");
        chromeDriver.findElement(By.id("user_password")).sendKeys("Jt1Z1xwS");
        chromeDriver.findElement(By.name("commit")).click();
        chromeDriver.findElement(By.linkText("Manual Promotions")).click();
        String str = "TestNG is working fine";
        assertEquals("TestNG is working fine", str);
    }

    public static void main(String[] args){

            //File file = new File("C:\\Program Files\\Odysii\\Odysii.CNC.Manager.exe.config");


    }
}
