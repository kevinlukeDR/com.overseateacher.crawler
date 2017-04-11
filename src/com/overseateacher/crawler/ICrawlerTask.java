package com.overseateacher.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * Created by Yiyu Jia on 11/28/16.
 * E-Mail:  yiyu.jia@iDataMining.org
 */
public interface ICrawlerTask {

    public CrawlController getController();

    public Class getCrawlerClass();

    public int getNumberOfCrawlers();

    public void setNumberOfCrawlers(int threadNum);

    public String getName();

}
