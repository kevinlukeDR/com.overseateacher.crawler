package com.overseateacher.crawler.sites.Ziprecruiter;

import com.overseateacher.crawler.Adapter.ToCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lu on 2016/12/6.
 */
public class ZRController {

    private static final Logger logger = LoggerFactory.getLogger(com.overseateacher.crawler.MainController.class);

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            logger.info("Needed parameter: ");
//            logger.info("\t rootFolder (it will contain intermediate crawl data)");
//            return;
//        }
        String sss = "D://Workspace//test.csv";
        ToCSV cc = new ToCSV(sss);
        cc.addLine("qwe,vxcv,cvx");
        cc.setName("vvv");
        System.out.print("werwer");
    /*
     * rootCrawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String rootCrawlStorageFolder = args[0];


        List<ZRCrawlerTask> taskList = getCrawlerTasks();

        Iterator<ZRCrawlerTask> taskIterator = taskList.iterator();
        while (taskIterator.hasNext()) {

            ZRCrawlerTask task = taskIterator.next();
            task.getController().startNonBlocking(task.getCrawlerClass(), task.getNumberOfCrawlers());
            task.getController().waitUntilFinish();
            logger.info(task.getName());

        }

    }

    private static List<ZRCrawlerTask> getCrawlerTasks() {

        List<ZRCrawlerTask> taskList = new ArrayList<ZRCrawlerTask>();

        taskList.add(new ZRTask()); //will get these classes name from configure file later.

        return taskList;

    }

}