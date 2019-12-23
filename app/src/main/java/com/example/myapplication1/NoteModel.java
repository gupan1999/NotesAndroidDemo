package com.example.myapplication1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteModel {

    public static List<Note> noteList = new ArrayList<Note>();
    public static List<Note> curList = new ArrayList<Note>();

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
