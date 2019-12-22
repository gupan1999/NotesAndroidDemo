package com.example.myapplication1;

import android.util.Log;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Note implements Serializable {
    private String title;
    private String content;
    private static long cnt=-1;
    private String loc;
    private int searchIndexTitle=0;
    private int searchIndexContent=0;
    private Date curTime;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public Note() {
    cnt++;
    loc="note_"+cnt;
    Log.d("loctest",loc);
    curTime=new Date(System.currentTimeMillis());
    }

    public int getSearchIndexTitle() {
        return searchIndexTitle;
    }

    public void setSearchIndexTitle(int searchIndexTitle) {
        this.searchIndexTitle = searchIndexTitle;
    }

    public int getSearchIndexContent() {
        return searchIndexContent;
    }

    public void setSearchIndexContent(int searchIndexContent) {
        this.searchIndexContent = searchIndexContent;
    }

    public Date getCurTime() {
        return curTime;
    }

    public void setCurTime(Date curTime) {
        this.curTime = curTime;
    }

    public void setCurTime(String curTime) throws ParseException {
        this.curTime = simpleDateFormat.parse(curTime);
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getCurTimestr() {
        return simpleDateFormat.format(curTime);
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
