package com.overseateacher.crawler.sites.postjobfree;

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
 * Created by Yiyu Jia on 11/29/16.
 * E-Mail:  yiyu.jia@iDataMining.org
 */
public class PostJobFreeCrawler  extends WebCrawler {
//    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
//
//    /**
//     * You should implement this function to specify whether the given url
//     * should be crawled or not (based on your crawling logic).
//     */
//    @Override
//    public boolean shouldVisit(Page referringPage, WebURL url) {
//        String href = url.getURL().toLowerCase();
//        // Ignore the url if it has an extension that matches our defined set of image extensions.
//        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
//            return false;
//        }
//
//        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
//        return href.startsWith("http://www.ics.uci.edu/");
//    }
//
//    /**
//     * This function is called when a page is fetched and ready to be processed
//     * by your program.
//     */
//    @Override
//    public void visit(Page page) {
//        int docid = page.getWebURL().getDocid();
//        String url = page.getWebURL().getURL();
//        String domain = page.getWebURL().getDomain();
//        String path = page.getWebURL().getPath();
//        String subDomain = page.getWebURL().getSubDomain();
//        String parentUrl = page.getWebURL().getParentUrl();
//        String anchor = page.getWebURL().getAnchor();
//
//        logger.debug("Docid: {}", docid);
//        logger.info("URL: {}", url);
//        logger.debug("Domain: '{}'", domain);
//        logger.debug("Sub-domain: '{}'", subDomain);
//        logger.debug("Path: '{}'", path);
//        logger.debug("Parent page: {}", parentUrl);
//        logger.debug("Anchor text: {}", anchor);
//
//        if (page.getParseData() instanceof HtmlParseData) {
//            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//            String text = htmlParseData.getText();
//            String html = htmlParseData.getHtml();
//            Set<WebURL> links = htmlParseData.getOutgoingUrls();
//
//            logger.debug("Text length: {}", text.length());
//            logger.debug("Html length: {}", html.length());
//            logger.debug("Number of outgoing links: {}", links.size());
//        }
//
//        Header[] responseHeaders = page.getFetchResponseHeaders();
//        if (responseHeaders != null) {
//            logger.debug("Response headers:");
//            for (Header header : responseHeaders) {
//                logger.debug("\t{}: {}", header.getName(), header.getValue());
//            }
//        }
//
//        logger.debug("=============");
//    }

    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private final static String URL_PREFIX = "https://www.postjobfree.com/resumes?q=esl+teacher&p=1";
    private final static Pattern URL_PARAMS_PATTERN = Pattern
            .compile("q=esl+teacher&p=\\d+");

    private final static String CSV_PATH = "D://Workspace//Test";
    private CsvWriter cw;
    private File csv;

    public PostJobFreeCrawler() throws IOException {
        csv = new File(CSV_PATH);

        if (csv.isFile()) {
            csv.delete();
        }

        cw = new CsvWriter(new FileWriter(csv, true), ',');
        cw.write("title");
        cw.write("brand");
        cw.write("newPrice");
        cw.write("oldPrice");
        cw.write("mileage");
        cw.write("age");
        cw.write("stage");
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

        String[] strs = href.split("\\?");
        if (strs.length < 2) {
            return false;
        }

        if (!URL_PARAMS_PATTERN.matcher(strs[1]).matches()) {
            return false;
        }

        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();

            Document doc = Jsoup.parse(html);
            String brand = doc.select("div.choose_item").first().text();

            Elements contents = doc.select("div.list_content");

            if (contents.size() == 20 && !url.contains("index=")) {
                return;
            } else {
                System.out.println("URL: " + url);
            }

            for (Element c : contents) {
                Element info = c.select(".list_content_carInfo").first();
                String title = info.select("h1").first().text();

                Elements prices = info.select(".list_content_price div");
                String newPrice = prices.get(0).text();
                String oldPrice = prices.get(1).text();

                Elements others = info.select(".list_content_other div");
                String mileage = others.get(0).select("ins").first().text();
                String age = others.get(1).select("ins").first().text();

                String stage = "unknown";
                if (c.select("i.car_tag_zhijian").size() != 0) {
                    stage = c.select("i.car_tag_zhijian").text();
                } else if (c.select("i.car_tag_yushou").size() != 0) {
                    stage = "presell";
                }

                try {
                    cw = new CsvWriter(new FileWriter(csv, true), ',');
                    cw.write(title);
                    cw.write(brand);
                    cw.write(newPrice.replaceAll("[￥万]", ""));
                    cw.write(oldPrice.replaceAll("[￥万]", ""));
                    cw.write(mileage);
                    cw.write(age);
                    cw.write(stage);
                    cw.endRecord();
                    cw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
