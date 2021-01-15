package com.febrian.fpgame;

public class Data {
    String username, password, bio,url_image;
    int score;

    public Data(){

    }

    public Data(String username, String password, String bio, String url_image, int score) {
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.url_image = url_image;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
