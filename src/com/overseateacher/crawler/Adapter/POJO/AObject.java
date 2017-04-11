package com.overseateacher.crawler.Adapter.POJO;

/**
 * Created by lu on 2016/12/18.
 */
public class AObject {
    private long fileId;
    private String fileName;
    private Attributes attributes;
    public AObject(){

    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
}

