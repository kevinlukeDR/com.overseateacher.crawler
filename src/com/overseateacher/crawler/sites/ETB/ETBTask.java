package com.overseateacher.crawler.sites.ETB;

import com.csvreader.CsvWriter;
import com.overseateacher.crawler.ICrawlerTask;
import com.overseateacher.crawler.S3.S3Client;
import com.overseateacher.crawler.sites.postjobfree.PostJobFreeCrawler;
import com.overseateacher.crawler.util.Configure;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

/**
 * Created by lu on 2016/12/14.
 */
public class ETBTask implements ICrawlerTask {

    public final String taskname = "ETB";
    private File file = new File("");
    public final String rootPath = file.getAbsolutePath();
    private static String day;
    CrawlController controller;
    PostJobFreeCrawler crawler;
    int threadNum = 1;
    private static S3Client s3Client = new S3Client();
    @Override
    public CrawlController getController() {
        if(controller == null)
            configTask();
        return controller;
    }

    @Override
    public Class getCrawlerClass() {
        if(crawler == null)
            configTask();
        return  PostJobFreeCrawler.class;
    }

    @Override
    public int getNumberOfCrawlers() {
        return threadNum;
    }

    @Override
    public void setNumberOfCrawlers(int threadNum) {
        this.threadNum = threadNum;
    }


    @Override
    public String getName() {
        return taskname;
    }

    private void Parser()throws Exception{
        String[] strings = Configure.getProperties("day");
        day = strings[0];
        File csv = new File("src/com/overseateacher/crawler/Documents/ESLCAFE/Resume.csv");
        CsvWriter cw = new CsvWriter(new FileWriter(csv, true), ',');
        Document doc = Jsoup.connect("http://www.eslteachersboard.com/cgi-bin/esl/index.pl?index")
                .data("ListSize", "Recent")
                .data("ListTimeA", day)
                .data("ListTimeB", "Day(s)")
                .data("StartDateA", "25")
                .data("StartDateB", "2")
                .data("StartDateC", "2012")
                .data("EndDateA", "30")
                .data("EndDateB", "0")
                .data("EndDateC", "2017")
                .data("KeySearch", "All")
                .data("Boolean", "Any")
                .data("Keywords", "")
                .data("Author", "")
                .post();

        Elements element = doc.select(".thread>a");
        for(Element e : element){
            cw.write(e.attr("href"));
            cw.endRecord();
            cw.close();
            cw = new CsvWriter(new FileWriter(csv, true), ',');
        }
        cw.endRecord();
        cw.close();
    }
    private void configTask(){

        //Parser
        try {
            File csv = new File("src/com/overseateacher/crawler/Documents/ESLCAFE/Resume.csv");
            if(csv.isFile()&&csv.exists())
                csv.delete();
            Parser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // put code for initializing controller, crawler here.
        //copy from other demo code first.

        String crawlStorageFolder = rootPath+"/src/com/overseateacher/crawler/Documents/Stuffs";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        // This website doesn't need to login
        // We can simply call http://www.eslcafe.com/jobs/wanted/index.cgi?read=343146&Username=geoffrey&Password=xiao
        config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(0);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(0);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(-1);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //folderName
//        s3Client.createFolder("ESL-Teachers-Board");
//        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
//        String folder = sdf.format(new java.util.Date());
//        s3Client.createFolder("ESL-Teachers-Board/"+folder);
    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        //www.eslcafe.com/jobs/wanted/index.cgi?index=&ListSize=Recent&ListTimeA=24&ListTimeB=Month(s)&KeySearch=Yes&Boolean=Any&Keywords=ESL
        try {
            String str = "";
            int count =0;
            BufferedReader br = new BufferedReader(new FileReader(rootPath+"/src/com/overseateacher/crawler/Documents/ESLCAFE/Resume.csv"));
            while ((str = br.readLine()) != null) {
//                if(count >4800){
                    String url = str;
                    controller.addSeed("http://www.eslteachersboard.com/"+url);
//                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(ETBCrawler.class, numberOfCrawlers);

    }


}