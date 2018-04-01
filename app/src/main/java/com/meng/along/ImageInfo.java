package com.meng.along;

import java.io.Serializable;

/**
 * Created by Long on 2018/3/20.
 * 图片信息
 */

public class ImageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String copyright;
    private String startdate;
    private String enddate;
    private String filename;
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



    public ImageInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }




}
