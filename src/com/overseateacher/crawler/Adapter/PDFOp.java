package com.overseateacher.crawler.Adapter;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.overseateacher.crawler.Adapter.POJO.AObject;
import com.overseateacher.crawler.Adapter.POJO.CSVFile;
import com.overseateacher.crawler.Adapter.POJO.PDFFile;
import org.jsoup.nodes.Element;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by lu on 2016/12/8.
 */
public abstract class PDFOp {
    /**
     * To csv csv file.
     *
     * @param aObject the a object
     * @return the csv file
     */
    abstract CSVFile toCSV(AObject aObject) ;

    /**
     * To pdf pdf file.
     *
     * @param aObject the a object
     * @return the pdf file
     */
    abstract PDFFile toPDF(AObject aObject);

    /**
     * Pdf writer.
     *
     * @param pdfFile the pdf file
     * @param name    the name
     * @param path    the path
     */
    public void PDFWriter(PDFFile pdfFile, String name, String path){
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        try {
            name = name.replaceAll("\"","").replace('\\',' ');
            PdfWriter.getInstance(document, new FileOutputStream(path + name+".pdf"));
            document.open();
            document.add(new Paragraph(pdfFile.toString()));
            DottedLineSeparator dottedline = new DottedLineSeparator();
            dottedline.setOffset(-2);
            dottedline.setGap(2f);
            Paragraph p = new Paragraph("");
            p.add(dottedline);
            document.add(p);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
