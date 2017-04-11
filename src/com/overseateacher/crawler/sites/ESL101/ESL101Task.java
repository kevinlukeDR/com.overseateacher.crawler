package com.overseateacher.crawler.sites.ESL101;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lu on 2017/2/28.
 */
public class ESL101Task implements ICrawlerTask {

    public final String taskname = "CAFE";
    private File file = new File("");
    public final String rootPath = file.getAbsolutePath();
    CrawlController controller;
    PostJobFreeCrawler crawler;
    int threadNum = 1;
    private static S3Client s3Client = new S3Client();
    private static String day;
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

    private void Parser(String day)throws Exception{
        String[] strings = Configure.getProperties("day");
        File csv = new File("src/com/overseateacher/crawler/Documents/ESLCAFE/ESL101.csv");
        CsvWriter cw = new CsvWriter(new FileWriter(csv, true), ',');
        Map<String, String> headers = new HashMap<>();
        headers.put("Host","www.esl101.com");
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
        headers.put("Accept","application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        headers.put("Accept-Encoding","gzip, deflate, br");
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("X-Requested-With","XMLHttpRequest");
        headers.put("Referer","https://www.esl101.com/teachers");
        headers.put("Content-Length","9437");
        headers.put("Cookie","_ga=GA1.2.335117126.1486420106; __uvt=; uvts=5cwGJOjs4d4N35vT; has_js=1; esl101_register_popup=complete; __atuvc=2%7C9; __atuvs=58b72964f4a5c916001; SSESSf7d59d32424d6d5f4ef3609fc640bc65=E90WmTI4IfQ0P0VzGL1uI8e7LBENl3oMYD2CgVVPdmo; _gat=1");
        headers.put("Connection","keep-alive");
        headers.put("Pragma","no-cache");
        headers.put("Cache-Control","no-cache");

        String string="", data,payload = "", name = "";
        Document doc;
        Elements element;
        int page = 0;
        JSONParser parser = new JSONParser();
        JSONArray jsonObject;
        JSONObject jsonObject1;
        while(page<1) {
            string = Jsoup.connect("https://www.esl101.com/views/ajax")
                    .headers(headers)
                    .ignoreContentType(true)
                    .requestBody("view_name=esl_teachers_search&view_display_id=page&view_args=&view_path=teachers&view_base_path=teachers&view_dom_id=a08ecbd8fd6a2c6a2d462364f7dd52a5&pager_element=0&page="+page+"&ajax_html_ids%5B%5D=uvTab&ajax_html_ids%5B%5D=uvTabLabel&ajax_html_ids%5B%5D=skip&ajax_html_ids%5B%5D=control-panel&ajax_html_ids%5B%5D=control-panel-inner&ajax_html_ids%5B%5D=block-views-esl_user_picture-block_1&ajax_html_ids%5B%5D=block-system-user-menu&ajax_html_ids%5B%5D=page&ajax_html_ids%5B%5D=page-inner&ajax_html_ids%5B%5D=navigation&ajax_html_ids%5B%5D=navigation-inner&ajax_html_ids%5B%5D=name-and-slogan&ajax_html_ids%5B%5D=site-name&ajax_html_ids%5B%5D=mobile-menu-button&ajax_html_ids%5B%5D=primary&ajax_html_ids%5B%5D=header&ajax_html_ids%5B%5D=header-inner&ajax_html_ids%5B%5D=main&ajax_html_ids%5B%5D=main-inner&ajax_html_ids%5B%5D=content&ajax_html_ids%5B%5D=content-inner&ajax_html_ids%5B%5D=content-header&ajax_html_ids%5B%5D=content-area&ajax_html_ids%5B%5D=sidebar-second&ajax_html_ids%5B%5D=sidebar-second-inner&ajax_html_ids%5B%5D=block-facetapi-dTysjQnSFGO8Ebjmr16iJDCEtL2lQV8J&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-nationality&ajax_html_ids%5B%5D=block-facetapi-UJ8x31J9jqSWjhQ160lGmh5tY57gRf6h&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-gender&ajax_html_ids%5B%5D=block-facetapi-qwXzDe4xAmkHwWGTcMPv2QV9f1AQH4bM&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-public-school-teacher&ajax_html_ids%5B%5D=block-facetapi-8EW5tjGC7lUY0I5zNcrXYNqMBuXRePBn&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-degree&ajax_html_ids%5B%5D=block-facetapi-aQpEpFXCOAldTTPQRNS5A0WLctpDOMgY&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-tesl-tefl-hours&ajax_html_ids%5B%5D=block-facetapi-0TWdp1rCXD9QaWxcJfZW0VLaPZjna0GZ&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-teach-experience&ajax_html_ids%5B%5D=block-facetapi-Uv99yN1ssOhdneCFA08keRAPgvydPAd0&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-want-teach&ajax_html_ids%5B%5D=block-facetapi-xvKgBrWGNOYpRyOrVBtH1zdJXUhgFoLv&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-student-age&ajax_html_ids%5B%5D=block-facetapi-4J7yT2W1XBdN4cSTv1tDuI0p2AeF5tXu&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-salary-range&ajax_html_ids%5B%5D=block-facetapi-2VA0RLEmPbs8sqSHks2VOndbNbErmWPp&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-location-current&ajax_html_ids%5B%5D=block-facetapi-1PvsSt3gUcet1PVyQCMvvKOrzvIyiLX0&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-transcripts&ajax_html_ids%5B%5D=block-facetapi-wHcWuljT9wdyJ1U2uBxNMNjeU1BWwM2G&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-crim-check&ajax_html_ids%5B%5D=block-facetapi-i61Jc51oBkT028GRdxMwN4eCGJHEUeFN&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-settings&ajax_html_ids%5B%5D=block-facetapi-8217p9DtLDWJp0p0ya0TSAJtdRaU53Vi&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-date-of-birth&ajax_html_ids%5B%5D=block-facetapi-cO3oVtAqYt8W1avp1i52aZrjPKYpvu8b&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-last-access&ajax_html_ids%5B%5D=block-facetapi-33l36IXJ35tQuHJopiIZKccllkbpiD1I&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-created&ajax_html_ids%5B%5D=block-facetapi-yN7Nwa5lqqglHzEwTn6geBchtpmcmhow&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-dependants&ajax_html_ids%5B%5D=block-facetapi-U0n4d0UvhiJBqL5659s0P9S3sVApGBPt&ajax_html_ids%5B%5D=facetapi-facet-search-apidefault-user-index-block-field-availability-datevalue&ajax_html_ids%5B%5D=block-views-esl_testimonials-block&ajax_html_ids%5B%5D=footer&ajax_html_ids%5B%5D=footer-inner&ajax_html_ids%5B%5D=block-boxes-esl_copyright&ajax_html_ids%5B%5D=boxes-box-esl_copyright&ajax_html_ids%5B%5D=block-menu-menu-about-esl101&ajax_html_ids%5B%5D=block-boxes-site_by_jibe&ajax_html_ids%5B%5D=boxes-box-site_by_jibe&ajax_html_ids%5B%5D=block-menu-menu-social-menu&ajax_html_ids%5B%5D=cboxOverlay&ajax_html_ids%5B%5D=colorbox&ajax_html_ids%5B%5D=cboxWrapper&ajax_html_ids%5B%5D=cboxTopLeft&ajax_html_ids%5B%5D=cboxTopCenter&ajax_html_ids%5B%5D=cboxTopRight&ajax_html_ids%5B%5D=cboxMiddleLeft&ajax_html_ids%5B%5D=cboxContent&ajax_html_ids%5B%5D=cboxTitle&ajax_html_ids%5B%5D=cboxCurrent&ajax_html_ids%5B%5D=cboxPrevious&ajax_html_ids%5B%5D=cboxNext&ajax_html_ids%5B%5D=cboxSlideshow&ajax_html_ids%5B%5D=cboxLoadingOverlay&ajax_html_ids%5B%5D=cboxLoadingGraphic&ajax_html_ids%5B%5D=cboxMiddleRight&ajax_html_ids%5B%5D=cboxBottomLeft&ajax_html_ids%5B%5D=cboxBottomCenter&ajax_html_ids%5B%5D=cboxBottomRight&ajax_page_state%5Btheme%5D=caviar&ajax_page_state%5Btheme_token%5D=2ZT0lBlD7WlMUtBqrKDTyyu-qqs_oQ-sjlT3l8aUoEs&ajax_page_state%5Bcss%5D%5Bmodules%2Fsystem%2Fsystem.base.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fsystem%2Fsystem.menus.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fsystem%2Fsystem.messages.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fsystem%2Fsystem.theme.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fbook%2Fbook.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fcomment%2Fcomment.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fdate%2Fdate_api%2Fdate.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fdate%2Fdate_popup%2Fthemes%2Fdatepicker.1.7.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Ffield%2Ftheme%2Ffield.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Flogintoboggan%2Flogintoboggan.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fnode%2Fnode.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fsearch%2Fsearch.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fuser%2Fuser.css%5D=1&ajax_page_state%5Bcss%5D%5Bmodules%2Fforum%2Fforum.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fviews%2Fcss%2Fviews.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Ftabs.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fcolorbox%2Fstyles%2Fdefault%2Fcolorbox_style.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fctools%2Fcss%2Fctools.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fflag%2Ftheme%2Fflag.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Ffacetapi%2Ffacetapi.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Ffonts%2Fstylesheet.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Ffonts%2Ficonic_stroke%2Ficonic_stroke.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Ffonts%2Ficonic_fill%2Ficonic_fill.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Fdefault.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Flayout.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Fstyle.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Fodometer%2Fodometer-theme-minimal.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fjquery.tagsinput.css%5D=1&ajax_page_state%5Bcss%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fcss%2Fprint.css%5D=1&ajax_page_state%5Bjs%5D%5B0%5D=1&ajax_page_state%5Bjs%5D%5B1%5D=1&ajax_page_state%5Bjs%5D%5B2%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fjquery.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fjquery.once.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fdrupal.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fjquery.cookie.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fjquery.form.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fajax.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Flibraries%2Fcolorbox%2Fjquery.colorbox-min.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fcolorbox%2Fjs%2Fcolorbox.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fcolorbox%2Fstyles%2Fdefault%2Fcolorbox_style.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fcolorbox%2Fjs%2Fcolorbox_load.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fcolorbox%2Fjs%2Fcolorbox_inline.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fmemcache%2Fmemcache_admin%2Fmemcache.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fflag%2Ftheme%2Fflag.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fviews_load_more%2Fviews_load_more.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fviews%2Fjs%2Fbase.js%5D=1&ajax_page_state%5Bjs%5D%5Bmisc%2Fprogress.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fviews%2Fjs%2Fajax_view.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Ffacetapi%2Ffacetapi.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fgoogle_analytics%2Fgoogleanalytics.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fmodules%2Fcontrib%2Fgoogle_analytics_et%2Fjs%2Fgoogle_analytics_et.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fjquery.bxslider.min.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fodometer.min.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fwaypoints.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fjquery.localscroll.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fjquery.scrollto.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fjquery.tagsinput.min.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fmodernizr.js%5D=1&ajax_page_state%5Bjs%5D%5Bsites%2Fall%2Fthemes%2Fcaviar%2Fjs%2Fcaviar.js%5D=1")
                    .method(Connection.Method.POST).execute().body();

            jsonObject = (JSONArray) parser.parse(string);
            if(page==0) {
                jsonObject1 = (JSONObject) jsonObject.get(2);
            }
            else{
                jsonObject1 = (JSONObject) jsonObject.get(1);
            }
            data = (String) jsonObject1.get("data");

            doc = Jsoup.parse(data, "UTF-8");

            if(doc.select(".view-empty>p").text().contains("Sorry")){
                break;
            }
            element = doc.select(".field.field-name-name>h5>a");
            for (Element e : element) {
                payload = e.attr("href");
                name = e.text();
                cw.write("https://www.esl101.com/" + payload);
                cw.endRecord();
                cw.close();
                cw = new CsvWriter(new FileWriter(csv, true), ',');
            }
            page++;
            if(page%10 == 0){
                System.out.println(page+" has been crawled");
            }
        }
        cw.endRecord();
        cw.close();
        System.out.println(page);
    }
    private void configTask() {

        //Parser
        try {
            File csv = new File("src/com/overseateacher/crawler/Documents/ESLCAFE/ESL101.csv");
            if (csv.isFile() && csv.exists())
                csv.delete();
            Parser(day);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // put code for initializing controller, crawler here.
        //copy from other demo code first.

        String crawlStorageFolder = rootPath + "/src/com/overseateacher/crawler/Documents/Stuffs";

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
//        s3Client.createFolder("Dave-ESL-Cafe");
//        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd" );
//        String folder = sdf.format(new java.util.Date());
//        s3Client.createFolder("Dave-ESL-Cafe/All");
    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        //www.eslcafe.com/jobs/wanted/index.cgi?index=&ListSize=Recent&ListTimeA=24&ListTimeB=Month(s)&KeySearch=Yes&Boolean=Any&Keywords=ESL
        try {
            String str = "";
            int count = 0;
            BufferedReader br = new BufferedReader(new FileReader(rootPath + "/src/com/overseateacher/crawler/Documents/ESLCAFE/ESL101.csv"));
            String url;
            while ((str = br.readLine()) != null) {
//                if(count >4800){
                url = str;
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
        controller.start(ESL101Crawler.class, numberOfCrawlers);
    }
}
