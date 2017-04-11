package com.overseateacher.crawler.util;

/**
 * Created by lu on 2016/12/6.
 */
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.crawler4j.robotstxt.HostDirectives;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtParser;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Util;

/**
 * @author Yasser Ganjisaffar
 */
public class TeachRobotstxtServer {

    private static final Logger logger = LoggerFactory.getLogger(TeachRobotstxtServer.class);

    protected RobotstxtConfig config;

    protected final Map<String, HostDirectives> host2directivesCache = new HashMap<>();

    protected TeachPageFetcher pageFetcher;

    public TeachRobotstxtServer(RobotstxtConfig config, TeachPageFetcher pageFetcher) {
        this.config = config;
        this.pageFetcher = pageFetcher;
    }

    private static String getHost(URL url) {
        return url.getHost().toLowerCase();
    }

    /** Please note that in the case of a bad URL, TRUE will be returned */
    public boolean allows(WebURL webURL) {
        if (config.isEnabled()) {
            try {
                URL url = new URL(webURL.getURL());
                String host = getHost(url);
                String path = url.getPath();

                HostDirectives directives = host2directivesCache.get(host);

                if ((directives != null) && directives.needsRefetch()) {
                    synchronized (host2directivesCache) {
                        host2directivesCache.remove(host);
                        directives = null;
                    }
                }

                if (directives == null) {
                    directives = fetchDirectives(url);
                }

                return directives.allows(path);
            } catch (MalformedURLException e) {
                logger.error("Bad URL in Robots.txt: " + webURL.getURL(), e);
            }
        }

        return true;
    }

    private HostDirectives fetchDirectives(URL url) {
        WebURL robotsTxtUrl = new WebURL();
        String host = getHost(url);
        String port = ((url.getPort() == url.getDefaultPort()) || (url.getPort() == -1)) ? "" :
                (":" + url.getPort());
        robotsTxtUrl.setURL("http://" + host + port + "/robots.txt");
        HostDirectives directives = null;
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(robotsTxtUrl);
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                Page page = new Page(robotsTxtUrl);
                fetchResult.fetchContent(page);
                String contentType = page.getContentType() == null ? "" : page.getContentType();
                if (Util.hasPlainTextContent(contentType) || contentType.length() == 0) {
                    String content;
                    if (page.getContentCharset() == null) {
                        content = new String(page.getContentData());
                    } else {
                        content = new String(page.getContentData(), page.getContentCharset());
                    }
                    directives = RobotstxtParser.parse(content, config.getUserAgentName());
                } else if (contentType.contains(
                        "html")) { // TODO This one should be upgraded to remove all html tags
                    String content = new String(page.getContentData());
                    directives = RobotstxtParser.parse(content, config.getUserAgentName());
                } else {
                    logger.warn(
                            "Can't read this robots.txt: {}  as it is not written in plain text, " +
                                    "contentType: {}",
                            robotsTxtUrl.getURL(), contentType);
                }
            } else {
                logger.debug("Can't read this robots.txt: {}  as it's status code is {}",
                        robotsTxtUrl.getURL(), fetchResult.getStatusCode());
            }
        } catch (SocketException | UnknownHostException | SocketTimeoutException |
                NoHttpResponseException se) {
            // No logging here, as it just means that robots.txt doesn't exist on this server
            // which is perfectly ok
        } catch (PageBiggerThanMaxSizeException pbtms) {
            logger.error("Error occurred while fetching (robots) url: {}, {}",
                    robotsTxtUrl.getURL(), pbtms.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while fetching (robots) url: " + robotsTxtUrl.getURL(), e);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }

        if (directives == null) {
            // We still need to have this object to keep track of the time we fetched it
            directives = new HostDirectives();
        }
        synchronized (host2directivesCache) {
            if (host2directivesCache.size() == config.getCacheSize()) {
                String minHost = null;
                long minAccessTime = Long.MAX_VALUE;
                for (Map.Entry<String, HostDirectives> entry : host2directivesCache.entrySet()) {
                    if (entry.getValue().getLastAccessTime() < minAccessTime) {
                        minAccessTime = entry.getValue().getLastAccessTime();
                        minHost = entry.getKey();
                    }
                }
                host2directivesCache.remove(minHost);
            }
            host2directivesCache.put(host, directives);
        }
        return directives;
    }
}
