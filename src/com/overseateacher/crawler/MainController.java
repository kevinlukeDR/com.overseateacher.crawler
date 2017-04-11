package com.overseateacher.crawler;



import com.overseateacher.crawler.sites.ESL101.ESL101Task;
import com.overseateacher.crawler.sites.ESLCAFE.CAFETask;
import com.overseateacher.crawler.sites.ESLJobFeed.EJFTask;
import com.overseateacher.crawler.sites.ETB.ETBTask;
import com.overseateacher.crawler.sites.TEFL.TEFLTask;
import com.overseateacher.crawler.sites.TMEA.TMEATask;
import com.overseateacher.crawler.sites.TOC.TOCTask;
import com.overseateacher.crawler.sites.indeed.IndeedTask;
import com.overseateacher.crawler.sites.jobs4jobs.Jobs4JobsParser.J4JPTask;
import com.overseateacher.crawler.sites.jobs4jobs.Jobs4jobsTask;
import com.overseateacher.crawler.sites.jobspider.JSTask;
import com.overseateacher.crawler.util.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by Yiyu Jia on 11/28/16.
 * E-Mail:  yiyu.jia@iDataMining.org
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
//        setProperties();
//        if (args.length != 1) {
//            logger.info("Needed parameter: ");
//            logger.info("\t rootFolder (it will contain intermediate crawl data)");
//            return;
//        }

    /*
     * rootCrawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
//        String rootCrawlStorageFolder = args[0];


        List<ICrawlerTask> taskList = getCrawlerTasks();

        Iterator<ICrawlerTask> taskIterator = taskList.iterator();

        while (taskIterator.hasNext()) {
            try {
                ICrawlerTask task = taskIterator.next();
                task.getController().waitUntilFinish();
                logger.info(task.getName());
            }catch (Exception e){
                continue;
            }
        }

    }

    private static void setProperties(){
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("config.properties");

            // set the properties value
            prop.setProperty("sites", "ESLCAFE");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    private static List<ICrawlerTask> getCrawlerTasks() {

        List<ICrawlerTask> taskList = new ArrayList<ICrawlerTask>();

        String[] sites = Configure.getProperties("sites");
        for(String site : sites){
            if(site.equals("ESLCAFE"))
                taskList.add(new CAFETask()); //will get these classes name from configure file later.
            else if(site.equals("TMEA"))
                taskList.add(new TMEATask());
            else if(site.equals("ESL101"))
                taskList.add(new ESL101Task());
            else if(site.equals("indeed"))
                taskList.add(new IndeedTask());
            else if(site.equals("jobs4jobs")){
                //TODO
                taskList.add(new Jobs4jobsTask());
                taskList.add(new J4JPTask());
            }
            else if(site.equals("jobspider"))
                taskList.add(new JSTask());
            else if(site.equals("ETB"))
                taskList.add(new ETBTask());
            else if(site.equals("EJF"))
                taskList.add(new EJFTask());
            else if(site.equals("TOC"))
                taskList.add(new TOCTask());
            else {
                taskList.add(new TEFLTask());
            }
        }
        return taskList;

    }

}