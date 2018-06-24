package com.odysii.test.impulse.transactionReport;

import com.odysii.PayloadHandler;
import com.odysii.db.DBHandler;
import com.odysii.test.impulse.serial.survey.helper.SurveyTestBase;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionReportTest extends SurveyTestBase{
//    private static final String TOP_SELECT = "SELECT TOP (5) [Id],[Payload] " +
//            "FROM [DW_qa].[dbo].[Transactions] where ProjectId = '2727' and TransactionDate >= '"+getCurrentDate()+"' order by ReportedTime desc";

    private static final String TOP_SELECT = "SELECT TOP (5) [Id],[Payload] " +
            "FROM [DW_qa].[dbo].[Transactions] where ProjectId = '2727' and TransactionDate >= '2018-06-03' order by ReportedTime desc";

    @Test
    public void test(){
        DBHandler dbHandler = new DBHandler();
        ResultSet resultSet = dbHandler.executeSelectQuery(TOP_SELECT);
        try {
            while (resultSet.next()){
                String payload = resultSet.getString("Payload");
                PayloadHandler payloadHandler = new PayloadHandler(payload);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getCurrentDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate.toString();
    }
}
