package com.odysii.selenium.cloudMI;

import com.odysii.selenium.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PriceBookManager extends PageObject {
    public PriceBookManager(WebDriver driver) {
        super(driver);
    }
    public void setPricebookLoaderType(PriceBookLoaderType type){
        clickOnCellWithinFeatureTable("PricebookLoaderType");
        WebElement element = webDriver.findElement(By.id("setting_value"));
        element.clear();
        element.sendKeys(type.getType());
        webDriver.findElement(By.name("commit")).click();
    }
    private void clickOnCellWithinFeatureTable(String parameter){
        List<WebElement> cols = webDriver.findElements(By.xpath(".//*[@class=\"table-light\"]/tbody/tr"));
        for (WebElement col : cols ){
           if (col.findElement(By.xpath("td[1]")).getText().equals(parameter)){
               col.findElement(By.xpath("td[4]/a")).click();
               break;
           }
        }
    }
//    public static void main(String[]args){
//        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
//        driver = new ChromeDriver();
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        driver.manage().window().maximize();
//        clickOnCellWithinFeatureTable("");
//    }
}
