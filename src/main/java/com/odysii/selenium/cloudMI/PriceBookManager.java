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
    static WebDriver driver;
    public PriceBookManager(WebDriver driver) {
        super(driver);
    }
    @FindBy(linkText="")
    private WebElement someElement;

    private static void clickOnCellWithinFeatureTable(String parameter){
        List<WebElement> cols = driver.findElements(By.xpath(".//*[@class=\"table-light\"]/tbody/tr"));
        for (WebElement col : cols ){
            col.findElement(By.xpath("td[1]")).getText();
        }
    }
    public static void main(String[]args){
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        clickOnCellWithinFeatureTable("");
    }
}
