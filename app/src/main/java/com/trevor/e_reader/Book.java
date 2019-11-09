package com.trevor.e_reader;

import java.util.Date;

public class Book {
    private String id;
    private String title;
    private String author;
    private String url;
    private String position;
    private Date lastRead;

    public Book(String title, String author, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
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
        return this.author;
    }

    public String getDateLastRead() {
        return "";
//        return this.lastRead;
    }

    public String getPosition() {
        return this.position;
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
        this.author = author;
    }

    public  void setDate(Date date) {
        this.lastRead = date;
    }
    public void setPosition(String position) {
        this.position = position;
    }


}
