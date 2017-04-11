package com.overseateacher.crawler.sites.TOC;

import com.csvreader.CsvWriter;
import com.overseateacher.crawler.S3.S3Client;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by lu on 2017/3/3.
 */
public class TOCCrawler extends WebCrawler {
    public static int count = 0;
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    //TODO We can use an automatic method to update kwe based on a list;
    private SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
    private String folder = sdf.format(new java.util.Date());
    private static final String PRE = "https://www.teachoverseas.ca/efl-resumes?id=";
    private CsvWriter cw;
    private File csv;
    private static S3Client s3Client = new S3Client();

    public TOCCrawler() throws IOException {

    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return href.startsWith(PRE);
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            try {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();
                Document doc = Jsoup.parse(html);
                UUID idOne = UUID.randomUUID();
                String email="";
                email = doc.select("#userEmail").text().trim();
                if(email.equals("") || !email.contains("@"))
                    return;
                String fileName = email+idOne+".html";
                fileName = fileName.replaceAll("@", "-AT-");
                fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "-");
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(doc.toString());
                fileWriter.flush();
                fileWriter.close();
                s3Client.uploadFile("Teach-Oversea-Canada/"+folder, fileName, file);
                file.delete();
//                com.itextpdf.text.Document document = new com.itextpdf.text.Document();

//                int temp= 0;
//                for(Element e: ele){
//                    if(temp == 1)
//                        name = e.text();
//                    temp++;
//                }
//                temp = 0;
//                PdfWriter.getInstance(document, new FileOutputStream(PDF_PATH + name+".pdf"));
//                document.open();
//                for(Element e : ele){
//                    e.text();
//                    document.add(new Paragraph(e.text()));
//                    if(temp%2 == 1)
//                        document.add(new Paragraph(" "));
//                    temp++;
//                }
//                document.close();
                if(count%10 == 0)
                    System.out.println(count+" pages have crawled");
                count++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
