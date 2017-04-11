package com.overseateacher.crawler.ConnectDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Created by lu on 2016/12/19.
 */
public class ConnectionManager {

    private static String url = "jdbc:mysql://teachoverseatrack.crfwxqeg9ype.us-east-1.rds.amazonaws.com:3306/hairong_candidates";
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String username = "hairong";
    private static String password = "0p;9ol8ik";
    private static Connection con;
    private static int count;

    public ConnectionManager(){
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
    }

    /**
     *
     * @param name
     * @param email
     * @param udate
     * @param source
     */
    public void putData(String name, String email, String udate, String source){
        try {
                UUID idOne = UUID.randomUUID();
                try{
                    String[] tempstr = udate.split("Date:");
                    udate = tempstr[1];
                    name = name.replaceAll("\"","").replace('\\',' ').replace("'","_");
                    String[] strs = name.split(" ");
                    java.util.Date date = stringToDate(udate);
                    Timestamp timestamp = new Timestamp(date.getTime());
                    if(email.equals("")){
                        System.out.println("Email is NULL!");
                        return;
                    }
                    String check = "SELECT * FROM emailCampain WHERE email='"+email+"'";
                    Statement checksm = con.createStatement();
                    ResultSet checkres = checksm.executeQuery(check);
                    if(checkres.next()){
                        System.out.println("Email exists!!");
                        return;
                    }
                    String sql = "INSERT INTO emailCampain SET fname='"+strs[0]+"', lname='"+ strs[strs.length-1]+
                            "', email='"+email+"', email_status=0, setup_time='"+timestamp+"', uuid='"+String.valueOf(idOne)+"', " +
                            "source='"+source.replaceAll("'","\\\\'")+"'";
                    Statement stmt = con.createStatement();
                    int res = stmt.executeUpdate(sql);
                    if(res == 0){
                        System.out.print("error");
                    }
                    System.out.println("Successfully");
                    count++;
                } catch (NumberFormatException e){
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }


        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }//end try

    }

    public ResultSet getData(String num) throws SQLException {
        String sql = "SELECT * FROM emailCampain WHERE email_status=0 ORDER BY setup_time DESC limit "+num;
        Statement stmt = con.createStatement();
        return stmt.executeQuery(sql);
    }

    public int updateStatus(String email) throws SQLException {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String sql = "UPDATE emailCampain SET email_status=1, sentemail_time='"+timestamp+"' WHERE email='"+email+"'";
        Statement stmt = con.createStatement();
        return stmt.executeUpdate(sql);
    }
    public Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return con;
    }

    public java.util.Date stringToDate(String udate) throws  ParseException {
        String[] utemp = udate.split(" ");
        String dd=utemp[1], mm=utemp[2], yy=utemp[3];
        if(Integer.parseInt(dd)<10)
            dd= "0"+Integer.parseInt(dd);
        if(mm.equals("January"))
            mm="01";
        else if(mm.equals("February"))
            mm="02";
        else if(mm.equals("March"))
            mm="03";
        else if(mm.equals("April"))
            mm="04";
        else if(mm.equals("May"))
            mm="05";
        else if(mm.equals("June"))
            mm="06";
        else if(mm.equals("July"))
            mm="07";
        else if(mm.equals("August"))
            mm="08";
        else if(mm.equals("September"))
            mm="09";
        else if(mm.equals("October"))
            mm="10";
        else if(mm.equals("November"))
            mm="11";
        else
            mm="12";
        String date = yy+"-"+mm+"-"+dd;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(date);
    }
}
