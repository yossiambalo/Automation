package com.odysii.test.impulse.transactionReport;

import com.odysii.PayloadHandler;
import com.odysii.db.DBHandler;
import com.odysii.test.impulse.serial.survey.helper.SurveyTestBase;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class TransactionReportTest extends SurveyTestBase{
    private static final Logger LOGGER = Logger.getLogger(TransactionReportTest.class);
    private static final String TOP_SELECT = "SELECT TOP (5) [Id],[Payload],TransactionOdysiiId " +
            "FROM [DW_qa].[dbo].[Transactions] where ProjectId = '2727' and TransactionDate >= '"+getCurrentDate()+"' order by ReportedTime desc";

//    private static final String TOP_SELECT = "SELECT TOP (5) [Id],[Payload] " +
//            "FROM [DW_qa].[dbo].[Transactions] where ProjectId = '2727' and TransactionDate >= '2018-06-03' order by ReportedTime desc";

    @Test
    public void _001_runTransactionAndVerifyInDW(){
        LOGGER.info("Logging an INFO-level message");
        DBHandler dbHandler = new DBHandler();
        ResultSet resultSet = dbHandler.executeSelectQuery(TOP_SELECT);
        boolean res = false;
        try {
            while (resultSet.next()){
                String payload = resultSet.getString("Payload");
                PayloadHandler payloadHandler = new PayloadHandler(payload);
                payloadHandler.setBaskets();
                List<Map<String,String>> baskets = payloadHandler.getBaskets();
                Map<String,String> basket = baskets.get(0);
                res = (basket.get("Description").equals("Coca Cola") && basket.get("Code").equals("12") && basket.get("Quantity").equals("1"));
            }
        } catch (SQLException e) {
             LOGGER.error(e.getMessage());
        }
        assertTrue(res);
    }
    @Test
    public void _002_runTransactionAndVerifyTransactionOdysiiId(){
        DBHandler dbHandler = new DBHandler();
        ResultSet resultSet = dbHandler.executeSelectQuery(TOP_SELECT);
        String payload = null;
        try {
            while (resultSet.next()){
                 payload = resultSet.getString("TransactionOdysiiId");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(payload);
    }
    private static String getCurrentDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate.toString();
    }
}
