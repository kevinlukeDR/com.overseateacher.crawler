package com.overseateacher.crawler.sites.ESL101;

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
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by lu on 2017/2/28.
 */
public class ESL101Crawler extends WebCrawler {
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
    public ESL101Crawler() throws IOException {

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
        try {
            String url = page.getWebURL().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("Host", "www.esl101.com");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
            headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Referer", "https://www.esl101.com/teachers");
            headers.put("Cookie", "_ga=GA1.2.335117126.1486420106; __uvt=; uvts=5cwGJOjs4d4N35vT; has_js=1; esl101_register_popup=complete; __atuvc=2%7C9; __atuvs=58b72964f4a5c916001; referral_data=a%3A4%3A%7Bs%3A3%3A%22uid%22%3Bs%3A5%3A%2284930%22%3Bs%3A9%3A%22timestamp%22%3Bi%3A1488399520%3Bs%3A2%3A%22ip%22%3Bs%3A12%3A%2224.60.113.19%22%3Bs%3A7%3A%22referer%22%3Bs%3A31%3A%22https%3A%2F%2Fwww.esl101.com%2Fteachers%22%3B%7D; _gat=1; SSESSf7d59d32424d6d5f4ef3609fc640bc65=E90WmTI4IfQ0P0VzGL1uI8e7LBENl3oMYD2CgVVPdmo");
            headers.put("Connection", "keep-alive");
            headers.put("Upgrade-Insecure-Requests", "1");
            headers.put("Pragma", "no-cache");
            headers.put("Cache-Control", "no-cache");
            Document doc = Jsoup.connect(url).headers(headers).get();
            UUID idOne = UUID.randomUUID();
            String fileName = idOne + ".html";
            fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "-");
            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(doc.toString());
            fileWriter.flush();
            fileWriter.close();
            s3Client.uploadFile("TEST/" + "Archive", fileName, file);
            file.delete();
            if (count % 10 == 0)
                System.out.println(count + " pages have crawled");
            count++;
        } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
