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
    // 북아이템의 스트링인 아이디(예를 들면 ISBN 같은), 컨스트럭터에는 안 넣었음
    private String bookId;
    //
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
    public void setBookId(String bookId) {this.bookId =bookId;}

    public String getThumbnail() { return thumbnail; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getStatus() { return status.toString(); }
    public String getPubdate() { return pubdate; }
    public String getBookId() {return bookId;}

}
