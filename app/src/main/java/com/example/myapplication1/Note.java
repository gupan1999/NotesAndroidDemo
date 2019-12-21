package com.example.myapplication1;

import android.util.Log;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String content;
    private static long cnt=-1;
    private String loc;
    public Note() {
    cnt++;
    loc="note_"+cnt;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static long getCnt() {
        return cnt;
    }

    public static void setCnt(long cnt) {
        Note.cnt = cnt;
    }

    public String getLoc() {
        return loc;
    }
    public void setLoc(String loc){
        this.loc=loc;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
