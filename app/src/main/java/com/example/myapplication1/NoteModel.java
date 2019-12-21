package com.example.myapplication1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NoteModel {

    public static List<Note> noteList = new ArrayList<Note>();

    public static String cutTextShowed(String text) {
        if (text != null&&text.length()>250) {
                return text.substring(0, 250);
            }
            return text;
        }

}
