package com.overseateacher.crawler.sites.Ziprecruiter;

import com.overseateacher.crawler.util.TeachCrawlerController;

/**
 * Created by lu on 2016/12/6.
 */
public interface ZRCrawlerTask {
    public TeachCrawlerController getController();

    public Class getCrawlerClass();

    public int getNumberOfCrawlers();

    public void setNumberOfCrawlers(int threadNum);

    public String getName();
}
