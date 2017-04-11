package com.overseateacher.crawler.sites.Ziprecruiter;

import com.csvreader.CsvWriter;
import com.overseateacher.crawler.util.TeachWebCrawler;
import edu.uci.ics.crawler4j.crawler.Page;
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
public class ZRCrawler extends TeachWebCrawler {
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
                    + "|png|tiff?|mid|mp2|mp3|mp4"
                    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    //TODO We can use an automatic method to update kwe based on a list;
    //http://www.jobs4jobs.com/employers/employers16.php?pagenum=1&categorye=&countrye=&citye=&kwe=ESL
    private final static String URL_PREFIX = "http://www.jobs4jobs.com/employers/employers16.php";
    private final static Pattern URL_PARAMS_PATTERN = Pattern
            .compile("category(.*)");
    private static final String URL_PATTERN = "http://www.jobs4jobs.com/employers/preview_search.php";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+%40[A-Z0-9.-]+\\.[A-Z]{2,6}(.*)", Pattern.CASE_INSENSITIVE);
    //TODO
    private final static String CSV_PATH = "D://Workspace//Test2.csv";
    private CsvWriter cw;
    private File csv;

    public ZRCrawler() throws IOException {
        csv = new File(CSV_PATH);

        if (csv.isFile()) {
            csv.delete();
        }

        cw = new CsvWriter(new FileWriter(csv, true), ',');
        cw.write("NAME");
//        cw.write("Career");
//        cw.write("Phone");
//        cw.write("City");
//        cw.write("Zip");
//        cw.write("State");
//        cw.write("Country");
//        cw.write("E-mail");
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
//        if (FILTERS.matcher(href).matches() || !href.startsWith(URL_PREFIX)) {
//            return false;
//        }
//
//        String[] strs = href.split("\\?");
//        if (strs.length < 2) {
//            return false;
//        }
////        if(URL_PATTERN.matcher(href).matches())
//
//        if (!URL_PARAMS_PATTERN.matcher(strs[1]).matches()){
//            return false;
//        }

        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        //TODO
//        String str = url.substring(60);
//        boolean sdf = VALID_EMAIL_ADDRESS_REGEX.matcher(str).matches();
////        if(VALID_EMAIL_ADDRESS_REGEX.matcher(str).matches()){
//            System.out.print("123");
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();


            Document doc = Jsoup.parse(html);
            //.s>a
            //html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td
            //html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[1]
            Elements elements = doc.select(".js_hoverpreview.showVisited.fBold.zrLink.ttCapitalize.result_header.zipresume_popover");
            StringBuilder sb = new StringBuilder();
            for(Element e: elements){
                String sss = e.select("strong").text();
                boolean zzz = e.select("strong").text().equals("E-mail:");
                if(e.select("strong").text().equals("Name:"))
                    sb.append(e.text()+"; ");

                if(e.select("strong").text().equals("E-mail:"))
                    sb.append(e.text());
            }
            try {
                cw = new CsvWriter(new FileWriter(csv, true), ',');
                if(!sb.toString().equals(""))
                    cw.write(sb.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String sss = "";
//                try {
//                    cw = new CsvWriter(new FileWriter(csv, true), ',');
//
//                for(Element e : elements){
//                    String test = e.select("strong").text();
//                    sss = e.attr("href");
//                    if(sss.contains("javascript")){
//                        cw.write(sss);
//                    }
//                }
//                String name = doc.select(".s>strong").text();
//                String career = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[2]").first().text();
//                String phone = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[3]").first().text();
//                String city = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[4]").first().text();
//                String zip = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[5]").first().text();
//                String state = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[6]").first().text();
//                String country = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[7]").first().text();
//                String email = doc.select("html/body/table[2]/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[3]/td/p[8]").first().text();


//                Elements contents = doc.select("div.list_content");

//                if (contents.size() == 20 && !url.contains("index=")) {
//                    return;
//                } else {
//                    System.out.println("URL: " + url);
//                }
//
//                for (Element c : contents) {
//                    Element info = c.select(".list_content_carInfo").first();
//                    String title = info.select("h1").first().text();
//
//                    Elements prices = info.select(".list_content_price div");
//                    String newPrice = prices.get(0).text();
//                    String oldPrice = prices.get(1).text();
//
//                    Elements others = info.select(".list_content_other div");
//                    String mileage = others.get(0).select("ins").first().text();
//                    String age = others.get(1).select("ins").first().text();
//
//                    String stage = "unknown";
//                    if (c.select("i.car_tag_zhijian").size() != 0) {
//                        stage = c.select("i.car_tag_zhijian").text();
//                    } else if (c.select("i.car_tag_yushou").size() != 0) {
//                        stage = "presell";
//                    }

//                try {
//                    cw = new CsvWriter(new FileWriter(csv, true), ',');
//                    cw.write(sss);
//                    cw.write(career);
////                        cw.write(newPrice.replaceAll("[￥万]", ""));
//                    cw.write(phone);
//                    cw.write(city);
//                    cw.write(zip);
//                    cw.write(state);
//                    cw.write(country);
//                    cw.write(email);

                cw.endRecord();
                cw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        }
    }

}