package com.meng.along;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Long on 2018/3/22.
 */

public class ImageText implements Serializable{
    private String text;
    private int imageId;
    private String imageUrl;
    private File file;
    private static final long serialVersionUID = 1L;
    @Override
    public int hashCode() {
        if(imageUrl==null)
            return 0;
        return  imageUrl.length();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ImageText)) {
            return false;
        }
        ImageText b = (ImageText)obj;
        if(this.imageUrl .equals( b.imageUrl)) {
            return true;
        }
        return false;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ImageText(String text, int imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public ImageText() {
    }

}
