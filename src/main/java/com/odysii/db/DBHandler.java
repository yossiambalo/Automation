package com.odysii.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBHandler {
    private String connectionUrl;
    private String classForname;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;

    public DBHandler(String connectionUrl, String classForname){
        this.connectionUrl = connectionUrl;
        this.classForname = classForname;
        connect();
    }
    private void connect(){
        try {
            Class.forName(classForname);
            con = DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void executeSelectQuery(String query){
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println(rs.getString(4) + " " + rs.getString(6));
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }finally {
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (rs != null) try { rs.close(); } catch(Exception e) {}
        }
    }
    public void closeConnection(){
        if (con != null) try { con.close(); } catch(Exception e) {}
    }
    public void executeDeleteQuery(String query){
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println(rs.getString(4) + " " + rs.getString(6));
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }finally {
            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
            if (rs != null) try { rs.close(); } catch(Exception e) {}
        }
    }
}
