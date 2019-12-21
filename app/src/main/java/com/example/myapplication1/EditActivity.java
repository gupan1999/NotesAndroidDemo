package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EditActivity extends AppCompatActivity {
    private EditText titleET;
    private EditText contentET;
    private Note note;
    private String savedTitle;
    private String savedContent;
    private String status;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        status = getIntent().getStringExtra("status");
        titleET=findViewById(R.id.editText3);
        contentET=findViewById(R.id.editText2);
        if(status.equals("saved note")) {
            note = NoteModel.noteList.get(index);
        }else if(status.equals("new note")){
            note=new Note();
        }
        index=getIntent().getIntExtra("index",-1);
        savedTitle=note.getTitle();
        savedContent=note.getContent();
        titleET.setText(savedTitle);
        contentET.setText(savedContent);
        Toast.makeText(this, status,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        save();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void save() {

        final String title =this.titleET.getText().toString() ;
        final String content=this.contentET.getText().toString();
        final String loc=note.getLoc();
        if(status.equals("new note")){
            if(title.equals("")&&content.equals(""))return;
            else {
                note.setTitle(title);
                note.setContent(content);
                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();
                NoteModel.noteList.add(note);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemChanged(index);
                saveNote(title,content);

            }
        }else if(status.equals("saved note")){
            if(title.equals("")&&content.equals("")) {
                NoteModel.noteList.remove(note);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemRemoved(index);
                //new File(note.getLoc()).delete();
                for(Note note:NoteModel.noteList){
                    System.out.println(note.getTitle()+"\n"+note.getContent());
                }
                Toast.makeText(this, "已删除空白",
                                      Toast.LENGTH_SHORT).show();
            }
            else if(!title.equals(savedTitle)||!content.equals(savedContent)){
                note.setTitle(title);
                note.setContent(content);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemChanged(index);
                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();
                saveNote(title,content);

            }
        }


    }

    private void saveNote(String title,String content){
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(note.getLoc(), Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(title+"\n");
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


