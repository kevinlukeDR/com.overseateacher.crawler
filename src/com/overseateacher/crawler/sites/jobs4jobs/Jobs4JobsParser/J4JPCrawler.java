package com.overseateacher.crawler.sites.jobs4jobs.Jobs4JobsParser;

import com.csvreader.CsvWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.overseateacher.crawler.S3.S3Client;
import com.overseateacher.crawler.sites.jobs4jobs.Jobs4jobsTask;
import com.overseateacher.crawler.util.Configure;
import org.jsoup.nodes.Document;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by lu on 2016/12/6.
 */
public class J4JPCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    //TODO We can use an automatic method to update kwe based on a list;
    //TODO
    private static int count=0;
    private CsvWriter cw;
    private File csv;
    private SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
    private String folder = sdf.format(new java.util.Date());
    private static S3Client s3Client = new S3Client();
    public J4JPCrawler() throws IOException {

    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        //TODO
        try {
            if (page.getParseData() instanceof HtmlParseData) {
                String url = page.getWebURL().toString();
                String[] cookie = Configure.getProperties("token");
                Document doc = Jsoup.connect(url)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0")
                        .referrer("http://www.jobs4jobs.com/employers/employersearch.php")
                        .cookie("PHPSESSID", Jobs4jobsTask.getCookie())
                        .maxBodySize(0)
                        .get();
                Elements elements = doc.select(".t3>tbody>tr>td");
                UUID idOne = UUID.randomUUID();
                String email = "";
                for (Element e : elements) {
                    if (e.select("p>strong").text().equals(""))
                        continue;
                    Elements eles = e.select("p");
                    for (Element ele : eles) {
                        String[] strs = ele.text().split(":");
                        if (strs[0].equals("E-mail")) {
                            String ss = ele.text();
                            email = ele.text().substring(7).trim();
                        }
                    }
                }
                if (email.equals("")) {
                    System.out.println("Email is empty!!");
                    return;
                }
                String fileName = email + idOne + ".html";
                fileName = fileName.replaceAll("@", "-AT-");
                fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "-");
                File file = new File(fileName);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(doc.toString());
                fileWriter.flush();
                fileWriter.close();
                s3Client.uploadFile("Jobs-4-Jobs/" + folder, fileName, file);
                file.delete();
                if (count % 10 == 0)
                    System.out.println(count + " pages have crawled");
                count++;

//                Elements elements = doc.select(".t3>tbody>tr>td");
//                StringBuilder sb = new StringBuilder();
//            Map<String, String> map = new HashMap<>();
//                String name = "";
//                for (Element e : elements) {
//                    if (e.select("p>strong").text().equals(""))
//                        continue;
//                    Elements eles = e.select("p");
//                    for (Element ele : eles) {
//                        String[] strs = ele.text().split(":");
//                        String title = "";
//                        String text = "";
//                        if (strs[0].equals("Email")) {
//                            title = "Email";
//                            text = ele.text().substring(7);
//                            if (!text.contains("teacher"))
//                                return;
//                        }
//                    else {
//                        title = strs[0];
//                        if(strs.length<2)
//                            text = "";
//                        else text = strs[1];
//                    }
//                    if(title.equals("Preferred Job Location") && !text.contains("China"))
//                        return;
//                    map.put(title, text);
//                        if (title.equals("Name"))
//                            name = text;
//                    }
//                }
//            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
//            try {
//                name = name.trim();
//                PdfWriter.getInstance(document, new FileOutputStream(PDF_PATH + name+".pdf"));
//                document.open();
//                for(Iterator it = map.keySet().iterator(); it.hasNext();){
//                    String key = (String) it.next();
//                    String value = map.get(key);
//                    /*
//                    This part is use to design parser by ":"
//                     */
//                    document.add(new Paragraph(key+":"));
//                    document.add(new Paragraph(value));
//                }
//                document.close();
//            } catch (DocumentException e) {
//                e.printStackTrace();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


