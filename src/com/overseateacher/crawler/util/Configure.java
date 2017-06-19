package com.overseateacher.crawler.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by lu on 2017/2/6.
 */
public class Configure {
    public Configure(){

    }

    public static String getProperty(String element){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "config.properties";
            input = new BufferedInputStream( new FileInputStream(filename));
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return null;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out

            System.out.println(prop.getProperty(element));
            return prop.getProperty(element);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String[] getProperties(String element){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "config.properties";
            input = new BufferedInputStream( new FileInputStream(filename));
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return null;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out

            System.out.println(prop.getProperty(element));
            String[] sites = prop.getProperty(element).split(";");
            return sites;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static void setProperties(String key, String value){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "config.properties";
            input = new BufferedInputStream( new FileInputStream(filename));
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out

            prop.setProperty(key, value);
            FileOutputStream out = new FileOutputStream("config.properties");
            prop.store(out, null);
            out.close();
            return;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }
}
