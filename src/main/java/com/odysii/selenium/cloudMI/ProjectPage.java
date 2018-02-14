package com.odysii.selenium.cloudMI;

import com.odysii.selenium.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProjectPage extends PageObject{

    @FindBy(linkText="CS4 Pricebook Manager")
    private WebElement pricebook_link;

    public ProjectPage(WebDriver driver) {
        super(driver);
    }

    public PriceBookManager getPriceBook(){
        this.pricebook_link.click();
        return new PriceBookManager(webDriver);
    }
}
