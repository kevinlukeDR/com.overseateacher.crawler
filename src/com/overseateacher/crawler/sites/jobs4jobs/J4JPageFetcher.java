package com.overseateacher.crawler.sites.jobs4jobs;

/**
 * Created by lu on 2017/3/30.
 */
import edu.uci.ics.crawler4j.crawler.Configurable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.BasicAuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.FormAuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.NtAuthInfo;
import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo.AuthenticationType;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.IdleConnectionMonitorThread;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class J4JPageFetcher extends PageFetcher {
    protected static final Logger logger = LoggerFactory.getLogger(J4JPageFetcher.class);
    private String cookie;
    protected PoolingHttpClientConnectionManager connectionManager;
    protected CloseableHttpClient httpClient;
    protected final Object mutex = new Object();
    protected long lastFetchTime = 0L;
    protected IdleConnectionMonitorThread connectionMonitorThread = null;

    public J4JPageFetcher(CrawlConfig config) {
        super(config);
        RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(false).setCookieSpec("default").setRedirectsEnabled(false).setSocketTimeout(config.getSocketTimeout()).setConnectTimeout(config.getConnectionTimeout()).build();
        RegistryBuilder connRegistryBuilder = RegistryBuilder.create();
        connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        if(config.isIncludeHttpsPages()) {
            try {
                SSLContext connRegistry = SSLContexts.custom().loadTrustMaterial((KeyStore)null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType) {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory clientBuilder = new SSLConnectionSocketFactory(connRegistry, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                connRegistryBuilder.register("https", clientBuilder);
            } catch (Exception var7) {
                logger.warn("Exception thrown while trying to register https");
                logger.debug("Stacktrace", var7);
            }
        }

        Registry connRegistry1 = connRegistryBuilder.build();
        this.connectionManager = new PoolingHttpClientConnectionManager(connRegistry1);
        this.connectionManager.setMaxTotal(config.getMaxTotalConnections());
        this.connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());
        HttpClientBuilder clientBuilder1 = HttpClientBuilder.create();
        clientBuilder1.setDefaultRequestConfig(requestConfig);
        clientBuilder1.setConnectionManager(this.connectionManager);
        clientBuilder1.setUserAgent(config.getUserAgentString());
        clientBuilder1.setDefaultHeaders(config.getDefaultHeaders());
        if(config.getProxyHost() != null) {
            if(config.getProxyUsername() != null) {
                BasicCredentialsProvider proxy = new BasicCredentialsProvider();
                proxy.setCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()), new UsernamePasswordCredentials(config.getProxyUsername(), config.getProxyPassword()));
                clientBuilder1.setDefaultCredentialsProvider(proxy);
            }

            HttpHost proxy1 = new HttpHost(config.getProxyHost(), config.getProxyPort());
            clientBuilder1.setProxy(proxy1);
            logger.debug("Working through Proxy: {}", proxy1.getHostName());
        }

        this.httpClient = clientBuilder1.build();
        if(config.getAuthInfos() != null && !config.getAuthInfos().isEmpty()) {
            this.doAuthetication(config.getAuthInfos());
        }

        if(this.connectionMonitorThread == null) {
            this.connectionMonitorThread = new IdleConnectionMonitorThread(this.connectionManager);
        }

        this.connectionMonitorThread.start();
    }

    private void doAuthetication(List<AuthInfo> authInfos) {
        Iterator var2 = authInfos.iterator();

        while(var2.hasNext()) {
            AuthInfo authInfo = (AuthInfo)var2.next();
            if(authInfo.getAuthenticationType() == AuthenticationType.BASIC_AUTHENTICATION) {
                this.doBasicLogin((BasicAuthInfo)authInfo);
            } else if(authInfo.getAuthenticationType() == AuthenticationType.NT_AUTHENTICATION) {
                this.doNtLogin((NtAuthInfo)authInfo);
            } else {
                this.doFormLogin((FormAuthInfo)authInfo);
            }
        }

    }

    public String getCookie(){
        return this.cookie;
    }
    private void doBasicLogin(BasicAuthInfo authInfo) {
        logger.info("BASIC authentication for: " + authInfo.getLoginTarget());
        HttpHost targetHost = new HttpHost(authInfo.getHost(), authInfo.getPort(), authInfo.getProtocol());
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(authInfo.getUsername(), authInfo.getPassword()));
        this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    private void doNtLogin(NtAuthInfo authInfo) {
        logger.info("NT authentication for: " + authInfo.getLoginTarget());
        HttpHost targetHost = new HttpHost(authInfo.getHost(), authInfo.getPort(), authInfo.getProtocol());
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

        try {
            credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new NTCredentials(authInfo.getUsername(), authInfo.getPassword(), InetAddress.getLocalHost().getHostName(), authInfo.getDomain()));
        } catch (UnknownHostException var5) {
            logger.error("Error creating NT credentials", var5);
        }

        this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    private void doFormLogin(FormAuthInfo authInfo) {
        logger.info("FORM authentication for: " + authInfo.getLoginTarget());
        String fullUri = authInfo.getProtocol() + "://" + authInfo.getHost() + ":" + authInfo.getPort() + authInfo.getLoginTarget();
        HttpPost httpPost = new HttpPost(fullUri);
        ArrayList formParams = new ArrayList();
        formParams.add(new BasicNameValuePair(authInfo.getUsernameFormStr(), authInfo.getUsername()));
        formParams.add(new BasicNameValuePair(authInfo.getPasswordFormStr(), authInfo.getPassword()));

        try {
            UrlEncodedFormEntity e = new UrlEncodedFormEntity(formParams, "UTF-8");
            httpPost.setEntity(e);
            HttpResponse r = this.httpClient.execute(httpPost);
            Header[] headers = r.getAllHeaders();
            String[] strings = headers[4].getValue().split("=");
            cookie = strings[1];
            logger.debug("Successfully Logged in with user: " + authInfo.getUsername() + " to: " + authInfo.getHost());
        } catch (UnsupportedEncodingException var6) {
            logger.error("Encountered a non supported encoding while trying to login to: " + authInfo.getHost(), var6);
        } catch (ClientProtocolException var7) {
            logger.error("While trying to login to: " + authInfo.getHost() + " - Client protocol not supported", var7);
        } catch (IOException var8) {
            logger.error("While trying to login to: " + authInfo.getHost() + " - Error making request", var8);
        }

    }

    public PageFetchResult fetchPage(WebURL webUrl) throws InterruptedException, IOException, PageBiggerThanMaxSizeException {
        PageFetchResult fetchResult = new PageFetchResult();
        String toFetchURL = webUrl.getURL();
        HttpUriRequest request = null;

        PageFetchResult uri2;
        try {
            request = this.newHttpUriRequest(toFetchURL);
            Object response = this.mutex;
            synchronized(this.mutex) {
                long statusCode = (new Date()).getTime();
                if(statusCode - this.lastFetchTime < (long)this.config.getPolitenessDelay()) {
                    Thread.sleep((long)this.config.getPolitenessDelay() - (statusCode - this.lastFetchTime));
                }

                this.lastFetchTime = (new Date()).getTime();
            }

            CloseableHttpResponse response1 = this.httpClient.execute(request);
            fetchResult.setEntity(response1.getEntity());
            fetchResult.setResponseHeaders(response1.getAllHeaders());
            int statusCode1 = response1.getStatusLine().getStatusCode();
            if(statusCode1 != 301 && statusCode1 != 302 && statusCode1 != 300 && statusCode1 != 303 && statusCode1 != 307 && statusCode1 != 308) {
                if(statusCode1 >= 200 && statusCode1 <= 299) {
                    fetchResult.setFetchedUrl(toFetchURL);
                    String uri1 = request.getURI().toString();
                    if(!uri1.equals(toFetchURL) && !URLCanonicalizer.getCanonicalURL(uri1).equals(toFetchURL)) {
                        fetchResult.setFetchedUrl(uri1);
                    }

                    if(fetchResult.getEntity() != null) {
                        long size1 = fetchResult.getEntity().getContentLength();
                        if(size1 == -1L) {
                            Header length = response1.getLastHeader("Content-Length");
                            if(length == null) {
                                length = response1.getLastHeader("Content-length");
                            }

                            if(length != null) {
                                size1 = (long)Integer.parseInt(length.getValue());
                            }
                        }

                        if(size1 > (long)this.config.getMaxDownloadSize()) {
                            response1.close();
                            throw new PageBiggerThanMaxSizeException(size1);
                        }
                    }
                }
            } else {
                Header uri = response1.getFirstHeader("Location");
                if(uri != null) {
                    String size = URLCanonicalizer.getCanonicalURL(uri.getValue(), toFetchURL);
                    fetchResult.setMovedToUrl(size);
                }
            }

            fetchResult.setStatusCode(statusCode1);
            uri2 = fetchResult;
        } finally {
            if(fetchResult.getEntity() == null && request != null) {
                request.abort();
            }

        }

        return uri2;
    }

    public synchronized void shutDown() {
        if(this.connectionMonitorThread != null) {
            this.connectionManager.shutdown();
            this.connectionMonitorThread.shutdown();
        }

    }

    protected HttpUriRequest newHttpUriRequest(String url) {
        return new HttpGet(url);
    }
}
