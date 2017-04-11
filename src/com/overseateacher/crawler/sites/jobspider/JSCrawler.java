package com.overseateacher.crawler.sites.jobspider;

import com.csvreader.CsvWriter;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by lu on 2016/12/4.
 */
public class JSCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private final static String URL_PREFIX = "http://www.jobspider.com/job/";
    private static final Pattern URL_PAGE_PATTERN =
            Pattern.compile("http://www.jobspider.com/job/resume-search-results.asp/words_teacher/searchtype_1/page_\\d+");
    private static final Pattern URL_PATTERN =
            Pattern.compile("http://www.jobspider.com/job/view-resume-\\d+.html");

    private final static String CSV_PATH = "D://Workspace//Test2.csv";
    private CsvWriter cw;
    private File csv;

    public JSCrawler() throws IOException {
        csv = new File(CSV_PATH);

        if (csv.isFile()) {
            csv.delete();
        }

        cw = new CsvWriter(new FileWriter(csv, true), ',');
        cw.write("Resume Id");
        cw.endRecord();
        cw.close();
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (FILTERS.matcher(href).matches() || !href.startsWith(URL_PREFIX)) {
            return false;
        }
//        String nurl = href.substring(29);

        if(!URL_PAGE_PATTERN.matcher(href).matches())
            return false;


        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        //TODO Use ResumeID to send email to candidates by robot, PATTERN is http://www.jobspider.com/job/Apply4Resume.asp?ResumeID=30824

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();


            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".StandardRow>a");
            StringBuilder sb = new StringBuilder();
            try {
                cw = new CsvWriter(new FileWriter(csv, true), ',');
                for (Element e : elements) {
                    String id = e.attr("href");
                    id = id.substring(17, id.length()-5);
                    cw.write(id);
                }
                cw.endRecord();
                cw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}