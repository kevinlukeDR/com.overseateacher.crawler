package com.overseateacher.crawler.sites.TOC;

import com.overseateacher.crawler.ICrawlerTask;
import com.overseateacher.crawler.sites.postjobfree.PostJobFreeCrawler;
import com.overseateacher.crawler.util.Configure;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;

/**
 * Created by lu on 2017/3/3.
 */
public class TOCTask implements ICrawlerTask {

    public final String taskname = "TOC";
    private File file = new File("");
    public final String rootPath = file.getAbsolutePath();
    CrawlController controller;
    PostJobFreeCrawler crawler;
    int threadNum = 1;

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

    private void configTask(){

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
        config.setMaxDepthOfCrawling(1);

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

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        //www.eslcafe.com/jobs/wanted/index.cgi?index=&ListSize=Recent&ListTimeA=24&ListTimeB=Month(s)&KeySearch=Yes&Boolean=Any&Keywords=ESL
        try {
            String[] strings = Configure.getProperties("TOC");
            int count = Integer.parseInt(strings[0]);
            int to = Integer.parseInt(strings[1]);
            while (count<= to) {
//                if(count >4800){
                String url = "https://www.teachoverseas.ca/efl-resumes/"+count;
                controller.addSeed(url);
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
        controller.start(TOCCrawler.class, numberOfCrawlers);

    }

}