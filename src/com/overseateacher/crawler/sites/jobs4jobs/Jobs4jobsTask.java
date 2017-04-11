package com.overseateacher.crawler.sites.jobs4jobs;

import com.overseateacher.crawler.ICrawlerTask;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.BasicAuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.FormAuthInfo;

import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by lu on 2016/12/2.
 */
public class Jobs4jobsTask  implements ICrawlerTask {

    public final String taskname = "Jobs4jobs";
    private static String cookie;
    private File file = new File("");
    public final String rootPath = file.getAbsolutePath();
    CrawlController controller;
    Jobs4jobsCrawler crawler;
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
        return  Jobs4jobsCrawler.class;
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
        String userName = "kevinluke1993@hotmail.com";
        String password = "1234qwer";
        String urlLogin = "http://www.jobs4jobs.com/employers/employers16.php";
        String nameUsername = "ename";
        String namePassword = "epass";

        CrawlConfig config = new CrawlConfig();
        AuthInfo authInfo1 = null;
        try {
            authInfo1 = new FormAuthInfo(userName, password, urlLogin,
                    nameUsername, namePassword);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        config.addAuthInfo(authInfo1);
//        AuthInfo authInfo2 = null;
//        try {
//            authInfo2 = new BasicAuthInfo(userName, password, urlLogin);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        config.addAuthInfo(authInfo2);
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
        config.setMaxDepthOfCrawling(-1);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(10);

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
        J4JPageFetcher pageFetcher = new J4JPageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
            cookie = pageFetcher.getCookie();
        } catch (Exception e) {
            e.printStackTrace();
        }


    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */

//        try {
//            String str = "";
//            BufferedReader br = new BufferedReader(new FileReader("D://Workspace//Test.csv"));
//            br.readLine();
//            while ((str = br.readLine()) != null) {
//                String[] strs = str.split(",");
//                for(String s : strs){
//                    s = s.substring(18, s.length()-2);
//                    controller.addSeed("http://www.jobs4jobs.com/employers/"+s);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        controller.addSeed("http://www.jobs4jobs.com/employers/employers16.php?pagenum=1&categorye=Education, Training, and Library&countrye=&citye=&kwe=");



    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(Jobs4jobsCrawler.class, numberOfCrawlers);

    }
    public static String getCookie(){
        return cookie;
    }

}