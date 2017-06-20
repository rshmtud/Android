package com.example.addon.memorygame.Model;

/**
 * Created by Roshan on 12/19/2016.
 */
public class UserProfile {

    private String userName;

    private int score;

    public UserProfile(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public UserProfile() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
