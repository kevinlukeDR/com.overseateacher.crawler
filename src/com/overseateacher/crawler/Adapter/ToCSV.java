package com.overseateacher.crawler.Adapter;

import com.csvreader.CsvWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lu on 2016/12/6.
 */
public class ToCSV {
    private CsvWriter cw;
    private File csv;
    public ToCSV(String path){
        csv = new File(path);
        if (csv.isFile()) {
            csv.delete();
        }

        try {
            cw = new CsvWriter(new FileWriter(csv, true), ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void addColume(String colname){
//        csv.
//    }

    public void setName(String newname){
        System.out.print(csv.getParent());
        csv.renameTo(new File(csv.getParent()+"\\"+newname+".csv"));
    }

    public void addLine(String str){
        try {
            System.out.println(csv.getName());
            cw = new CsvWriter(new FileWriter(csv, true), ',');
            String[] strs = str.split(",");
            for (String s : strs) {
                cw.write(s);
            }
            cw.endRecord();
            cw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
