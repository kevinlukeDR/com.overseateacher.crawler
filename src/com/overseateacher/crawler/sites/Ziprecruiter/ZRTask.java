package com.overseateacher.crawler.sites.Ziprecruiter;

import com.overseateacher.crawler.sites.jobs4jobs.Jobs4jobsCrawler;
import com.overseateacher.crawler.sites.postjobfree.PostJobFreeCrawler;
import com.overseateacher.crawler.util.TeachCrawlerController;
import com.overseateacher.crawler.util.TeachFormAuthInfo;
import com.overseateacher.crawler.util.TeachPageFetcher;
import com.overseateacher.crawler.util.TeachRobotstxtServer;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;

import java.net.MalformedURLException;

/**
 * Created by lu on 2016/12/4.
 */
public class ZRTask implements ZRCrawlerTask {

    public final String taskname = "ZipRecuriter";

    TeachCrawlerController controller;
    PostJobFreeCrawler crawler;
    int threadNum = 1;

    @Override
    public TeachCrawlerController getController() {
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
        String crawlStorageFolder = "D:/Workplace/Test2";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 1;
        //TODO All kinds of authentication are failed
        String userName = "837170006@qq.com";
        String password = "1234qwer.";
        String urlLogin = "https://www.ziprecruiter.com/login";
        String nameUsername = "email";
        String namePassword = "password";

        CrawlConfig config = new CrawlConfig();
        TeachFormAuthInfo authInfo1 = null;
        try {
            authInfo1 = new TeachFormAuthInfo(userName, password, urlLogin,
                    nameUsername, namePassword);
            authInfo1.addParam("realm","members");
            authInfo1.addParam("next_url", "");
            authInfo1.addParam("needed", "");
            authInfo1.addParam("elqCustomerGUID", "");
            authInfo1.addParam("amazon_redirect_token", "0");
            authInfo1.addParam("ajax", "0");
            authInfo1.addParam("submitted", "Sign+In");
            authInfo1.addParam("_token", "U2FsdGVkX1+BySIpMUZ+D3d57zEswmbD3cZQ+uEREE8Ooqqvre96cxEfT8gb8vLYO6LaZ8cBngikhDNQctZrwNIRFSMD5b70");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        config.addAuthInfo(authInfo1);

        config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(1000);

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
        //TODO how to handle https login page first?
        config.setIncludeHttpsPages(true);
        config.toString();
    /*
     * Instantiate the controller for this crawl.
     */
        TeachPageFetcher pageFetcher = new TeachPageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        TeachRobotstxtServer robotstxtServer = new TeachRobotstxtServer(robotstxtConfig, pageFetcher);
        TeachCrawlerController controller = null;
        try {
            controller = new TeachCrawlerController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */

        controller.addSeed("https://www.ziprecruiter.com/resume-database/search?q=ESL&jobId=&loc=&latitude=&longitude=&city=&state=&postalCode=&country=&radiusSelect=30&resumePostedWithinSelect=30&minimumDegreeSelect=&experienceSelect=0&maxExperienceSelect=0&page=2");



    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(ZRCrawler.class, numberOfCrawlers);

    }

}