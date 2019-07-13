package com.example.libraryapp;

enum BookStatus {
    Available,
    Unavailable,
    Borrowed,
    Overdue,
    OnDelivery,
    Occupied
}

public class BookItem {
    private String thumbnail;
    private String title;
    private String author;
    private String publisher;
    private String pubdate;
    private BookStatus status = BookStatus.Available;
    public BookItem(String thumbnail, String title, String author, String publisher, String pubdate) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubdate = pubdate;
    }
    public void setStatus(BookStatus status) { this.status = status; }
    public String getThumbnail() { return thumbnail; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getStatus() { return status.toString(); }
    public String getPubdate() { return pubdate; }
}
