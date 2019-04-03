package com.odysii.test;

import com.odysii.db.DBHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class TestNGSample {
    WebDriver chromeDriver;
    @Test
    public void testCloudMI() {
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        System.setProperty("webdriver.chrome.driver", "C:\\chrome\\chromedriver.exe");
        chromeDriver = new ChromeDriver();
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

    @AfterClass
    public void clean() {
        chromeDriver.quit();
    }

    public static void main(String[] args) {
        DBHandler dbHandler = new DBHandler();
        //dbHandler.executeSelectQuery("SELECT [Id],[ProjectId],[SurveyTime],[SurveyDate],[SurveyId],[OptionId] FROM [DW_qa].[dbo].[SurveyJournal] where ChannelId='8766'");
        dbHandler.executeDeleteQuery("delete FROM [DW_qa].[dbo].[SurveyJournal] where ChannelId='8766'");
        dbHandler.closeConnection();
    }
    private static void connect(){
        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://10.28.76.71:1433;databaseName=DW_qa;user=sa;password=Gladiator01";

        // Declare the JDBC objects.
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Establish the connection.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl);

            // Create and execute an SQL statement that returns some data.
            String SQL = "SELECT TOP 10 * FROM [DW_qa].[dbo].[SurveyJournal]";
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println(rs.getString(4) + " " + rs.getString(6));
            }
        }

        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (rs != null) try { rs.close(); } catch(Exception e) {}
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (con != null) try { con.close(); } catch(Exception e) {}
        }
    }
}
