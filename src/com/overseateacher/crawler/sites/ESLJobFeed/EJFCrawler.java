package com.overseateacher.crawler.sites.ESLJobFeed;

import com.csvreader.CsvWriter;
import com.overseateacher.crawler.S3.S3Client;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by lu on 2017/1/18.
 */
public class EJFCrawler extends WebCrawler {
    public static int count = 0;
    private File file = new File("");
    public final String HTML_PATH = file.getAbsolutePath()+"src/com/overseateacher/crawler/Documents/ESLCAFE/HTML";
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    //TODO We can use an automatic method to update kwe based on a list;
    private SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
    private String folder = sdf.format(new java.util.Date());
    private static final String URL_PREFIX = "https://www.esljobfeed.com/esl-tesl-resumes?id=";
    private static final String URL_PREFIX2 = "http://www.esljobfeed.com/esl-tesl-resumes/";
    private CsvWriter cw;
    private File csv;
    private static S3Client s3Client = new S3Client();

    public EJFCrawler() throws IOException {

    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean test = href.startsWith(URL_PREFIX);
        return test;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            try {
                String url = page.getWebURL().toString();
                Document doc = Jsoup.connect(url)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .get();
                UUID idOne = UUID.randomUUID();
                String email="";
                email = doc.select("#userEmail").text();
                if(email.equals("")){
                    System.out.println("Email is Empty!!");
                    return;
                }
                String fileName = email+idOne+".html";
                fileName = fileName.replaceAll("@", "-AT-");
                fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "-");
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(doc.toString());
                fileWriter.flush();
                fileWriter.close();
                s3Client.uploadFile("ESL-Job-Feed/"+folder, fileName, file);
                file.delete();
                if(count%10 == 0)
                    System.out.println(count+" pages have crawled");
                count++;
            }catch (IOException e){
                e.printStackTrace();
            }
//            Element elements = doc.body();
//            Elements ele = elements.getElementsByTag("blockquote").first().getElementsByTag("p");
//            String name = "";
//
//            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//            try {
//                name = doc.select("html>body>font>p>strong>big").text();
//                name = name.replaceAll("\"","").replace('\\',' ');
//                PdfWriter.getInstance(document, new FileOutputStream(HTML_PATH + name+".pdf"));
//                document.open();
//                for(Element e: ele){
//                    document.add(new Paragraph(e.text()));
//                    document.add(new Paragraph(" "));
//                }
//                document.close();
//                if(count%10 == 0)
//                    System.out.println(count+" pages have crawled");
//                count++;
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }
    }
}


