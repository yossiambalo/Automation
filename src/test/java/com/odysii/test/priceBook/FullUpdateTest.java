package com.odysii.test.priceBook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

public class FullUpdateTest {

    @BeforeClass
    public void init(){
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        WebDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        chromeDriver.manage().window().maximize();
        chromeDriver.get("http://cloudmiqa.tveez.local/projects/51");
        chromeDriver.findElement(By.id("user_email")).sendKeys("yossi.ambalo@odysii.com");
        chromeDriver.findElement(By.id("user_password")).sendKeys("Jt1Z1xwS");
        chromeDriver.findElement(By.name("commit")).click();
        chromeDriver.findElement(By.linkText("CS4 Pricebook Manager")).click();
        
    }
}
