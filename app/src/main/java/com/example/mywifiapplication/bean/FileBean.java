package com.example.mywifiapplication.bean;

import java.io.Serializable;

public class FileBean implements Serializable {

    private static final long serialVersionUID = 8251451691675741027L;
    private String path;

    private String name;

    private long length;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
