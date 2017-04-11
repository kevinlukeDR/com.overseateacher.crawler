package com.overseateacher.crawler.util;

import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.util.*;

import javax.swing.text.html.FormSubmitEvent.MethodType;

/**
 * Created by Yiyu Jia on 12/5/16.
 * E-Mail:  yiyu.jia@iDataMining.org
 * <p>
 * FormAuthInfo contains the authentication information needed for FORM authentication (extending
 * AuthInfo which has
 * all common auth info in it)
 * Basically, this is the most common authentication, where you will get to a site and you will
 * need to enter a
 * username and password into an HTML form
 */
public class TeachFormAuthInfo extends AuthInfo {

    private String usernameFormStr;
    private String passwordFormStr;

    List<NameValuePair> params;

    /**
     * Constructor
     *
     * @param username        Username to login with
     * @param password        Password to login with
     * @param loginUrl        Full login URL, starting with "http"... ending with the full URL
     * @param usernameFormStr "Name" attribute of the username form field
     * @param passwordFormStr "Name" attribute of the password form field
     * @throws MalformedURLException Make sure your URL is valid
     */
    public TeachFormAuthInfo(String username, String password, String loginUrl, String usernameFormStr,
                             String passwordFormStr) throws MalformedURLException {
        super(AuthInfo.AuthenticationType.FORM_AUTHENTICATION, MethodType.POST, loginUrl, username,
                password);

        this.usernameFormStr = usernameFormStr;
        this.passwordFormStr = passwordFormStr;

        params = new ArrayList<>();
        params.add(new BasicNameValuePair(this.usernameFormStr, this.passwordFormStr));

    }

    /**
     * @return username html "name" form attribute
     */
    public String getUsernameFormStr() {
        return usernameFormStr;
    }

    /**
     * @param usernameFormStr username html "name" form attribute
     */
    public void setUsernameFormStr(String usernameFormStr) {
        this.usernameFormStr = usernameFormStr;
    }

    /**
     * @return password html "name" form attribute
     */
    public String getPasswordFormStr() {
        return passwordFormStr;
    }

    /**
     * not elegant design. But, once did something like
     * AuthInfo authForm = new TeachFormAuthInfo("myuser", "mypwd", "yourURL", "loginName", "password");
     * call authForm.addParameter("_token", "*********");
     * authForm.addParameter("realm", "members"); etc.
     *
    */
    public void addParam(String paraName, String value){

        params.add(new BasicNameValuePair(paraName, value));
    }

    /**
     * @param passwordFormStr password html "name" form attribute
     */
    public void setPasswordFormStr(String passwordFormStr) {
        this.passwordFormStr = passwordFormStr;
    }

    public List<NameValuePair> getParameterPairs() {

        return params;

    }
}