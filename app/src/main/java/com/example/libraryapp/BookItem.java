package com.example.libraryapp;

public class BookItem {
    private String thumbnail;
    private String title;
    private String author;
    private String publisher;
    private String pubdate;
    private String isbn;
    private String owner = "";
    private String borrower = "";
    private BookStatus status = BookStatus.Available;
    private double averageStar = 2.5;
    private int value = 1;
    public BookItem(String thumbnail, String title, String author, String publisher, String pubdate, String isbn, String owner) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubdate = pubdate;
        this.isbn = isbn;
        this.owner = owner;
    }
    enum BookStatus {
        Available,
        Unavailable,
        Borrowed,
        Overdue,
        OnDelivery,
        Occupied
    }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setISBN(String isbn) { this.isbn = isbn; }
    public void setAverageStar(double averageStar) { this.averageStar = averageStar; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setBorrower(String borrower) { this.borrower = borrower; }
    public void setValue(int value) { this.value = value; }
    public String getThumbnail() { return thumbnail; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getStatus() { return status.toString(); }
    public String getPubdate() { return pubdate; }
    public String getIsbn() { return isbn; }
    public double getAverageStar() { return averageStar; }
    public String getOwner() { return owner; }
    public String getBorrower() { return borrower; }
    public int getValue() { return value; }
    public BookStatus parseStatus(String status) {
        if (status.equals("Available")) {
            return BookStatus.Available;
        } else if (status.equals("Unavailable")) {
            return BookStatus.Unavailable;
        } else if (status.equals("Borrowed")) {
            return BookStatus.Borrowed;
        } else if (status.equals("Overdue")) {
            return BookStatus.Overdue;
        } else if (status.equals("OnDelivery")) {
            return BookStatus.OnDelivery;
        } else if (status.equals("Occupied")) {
            return BookStatus.Occupied;
        }
        return null;
    }
}
