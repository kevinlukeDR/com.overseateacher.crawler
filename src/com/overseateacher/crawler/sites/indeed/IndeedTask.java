package com.overseateacher.crawler.sites.indeed;

import com.overseateacher.crawler.ICrawlerTask;
import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * Created by Yiyu Jia on 11/28/16.
 * E-Mail:  yiyu.jia@iDataMining.org
 */
public class IndeedTask implements ICrawlerTask {


    @Override
    public CrawlController getController() {
        return null;
    }

    @Override
    public Class getCrawlerClass() {
        return null;
    }

    @Override
    public int getNumberOfCrawlers() {
        return 0;
    }

    @Override
    public void setNumberOfCrawlers(int threadNum) {

    }

    @Override
    public String getName() {
        return null;
    }

}
