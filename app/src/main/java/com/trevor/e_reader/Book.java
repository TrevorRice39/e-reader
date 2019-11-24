package com.trevor.e_reader;

import java.util.Date;

public class Book {
    private String id;
    private String title;
    private String author;
    private String url;
    private int position;
    private Date lastRead;
    private String path;

    public Book(String title, String author, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.lastRead = new Date(0L);
    }

    public Book() { }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getUrl() {
        return this.url;
    }

    public Date getDateLastRead() {
        return this.lastRead;
    }

    public int getPosition() {
        return this.position;
    }

    public String getPath() {
        return this.path;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public  void setDate(Date date) {
        this.lastRead = date;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
