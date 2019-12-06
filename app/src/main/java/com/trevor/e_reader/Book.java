package com.trevor.e_reader;

import java.util.Date;

// class to hold a book
public class Book {
    private String id; // id in db
    private String title; // title of book
    private String author; // author of book
    private String url; // url of book
    private int position; // page in book user is on
    private Date lastRead; // when it was last opened
    private String path; // path to file on phone

    public Book(String title, String author, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.lastRead = new Date(0L); // lowest date
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
