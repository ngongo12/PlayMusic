package com.ps14237.model;

import java.util.ArrayList;

public class ServerResponse {
    private User user;
    private String result, message;
    private ArrayList<Song> songs;
    private ArrayList<Singer> singers;
    private ArrayList<Integer> favoriteArr;

    public ArrayList<Integer> getFavoriteArr() {
        return favoriteArr;
    }

    public void setFavoriteArr(ArrayList<Integer> favoriteArr) {
        this.favoriteArr = favoriteArr;
    }




    public ArrayList<Singer> getSingers() {
        return singers;
    }

    public void setSingers(ArrayList<Singer> singers) {
        this.singers = singers;
    }



    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
