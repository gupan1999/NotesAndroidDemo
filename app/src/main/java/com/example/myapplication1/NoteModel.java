package com.example.myapplication1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteModel {

    public static List<Note> noteList = new ArrayList<Note>();
    public static List<Note> curList = new ArrayList<Note>();

    public static String cutTextShowed(String text,int begin) {
        int n=0;
        int newBegin=0;
        int newEnd=text.length();
        for(int i=0;i<text.length()-begin;i++){
            char ch=text.charAt(begin+i);
            if(ch=='\n') n += 20;
            else n+=1;
            if (n>200) {
                newEnd=begin+i;
                break;
                }
            }
        n=0;
        for(int i=0;i<begin;i++){
            char ch=text.charAt(begin-i);
            if(ch=='\n') n += 20;
            else n+=1;
            if(n>40) {
                newBegin=begin-i;
                break;
                }

             }
            return text.substring(newBegin,newEnd);
        }

    public static void sortByLastEditTime(List<Note>noteList){
         Collections.sort(noteList,new Comparator<Note>() {
            @Override
            public int compare(Note n1, Note n2) {
                if(n1.getCurTime().compareTo(n2.getCurTime())<0)return 1;
                else if(n1.getCurTime().compareTo(n2.getCurTime())>0)return -1;
                else return 0;
            }
        });
    }

}
