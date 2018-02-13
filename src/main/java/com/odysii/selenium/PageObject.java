package com.odysii.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PageObject {
    protected WebDriver webDriver;

    public PageObject(WebDriver driver){
        this.webDriver = driver;
        PageFactory.initElements(driver,this);
    }
}
