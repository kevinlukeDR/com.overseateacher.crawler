package com.overseateacher.crawler.sites.ESLCAFE;

import com.csvreader.CsvWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.overseateacher.crawler.S3.S3Client;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by lu on 2016/12/14.
 */
public class CAFECrawler extends WebCrawler {
    /**
     * The constant count.
     */
    public static int count = 0;
    private File file = new File("");
    /**
     * The Html path.
     */
    public final String HTML_PATH = file.getAbsolutePath()+"src/com/overseateacher/crawler/Documents/ESLCAFE/HTML";
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    //TODO We can use an automatic method to update kwe based on a list;
    private SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
    private String folder = sdf.format(new java.util.Date());
    private static final Pattern URL_PAGE_PATTERN =
            Pattern.compile("http://www.eslcafe.com/jobs/wanted/index.cgi?read=\\d+");
    private CsvWriter cw;
    private File csv;
    private static S3Client s3Client = new S3Client();

    /**
     * Instantiates a new Cafe crawler.
     *
     * @throws IOException the io exception
     */
    public CAFECrawler() throws IOException {

    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
//        String href = url.getURL().toLowerCase();
//        if(URL_PAGE_PATTERN.matcher(href).matches()){
//            return true;
//        }
//        return false;
        return true;
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
                email = doc.select("html>body>font>p>strong>a").text();
                if(email.equals(""))
                    return;
                String fileName = email+idOne+".html";
                fileName = fileName.replaceAll("@", "-AT-");
                fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "-");
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(doc.toString());
                fileWriter.flush();
                fileWriter.close();
                s3Client.uploadFile("Dave-ESL-Cafe/"+folder, fileName, file);
                file.delete();
                if(count%10 == 0)
                    System.out.println(count+" pages have crawled"+"    ESLCAFE");
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


