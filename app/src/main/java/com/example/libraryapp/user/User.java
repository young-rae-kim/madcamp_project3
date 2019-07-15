package com.example.libraryapp.user;

import java.util.HashMap;

public class User {
    private String userName;
    private String userId;
    private String userMail;
    private HashMap<String, Double> userRating; // 스트링인 책 아이디에 해당하는 유저의 점수 hashmap

    public User(String userName, String  userId, String userMail,HashMap<String,Double> userRating){
        this.userName = userName;
        this.userId =userId;
        this.userMail =userMail;
        this.userRating =userRating;
    }
    public String getUserName(){return userName;}
    public String getUserId(){return userId;}
    public String getUserMail(){return userMail;}
    public HashMap<String,Double> getUserRating(){return userRating;}

    public void setUserName(String userName){this.userName =userName;}
    public void setUserId(String userId){this.userId = userId;}
    public void setUserMail(String userMail){this.userMail=userMail;}
    public void setUserRating(HashMap<String, Double> userRating){this.userRating =userRating;}


}
