package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private EditText titleET;
    private EditText contentET;
    private Note note;
    private String savedTitle;
    private String savedContent;
    private String status;
    private int index;
    private TextView time;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        toolbar=findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        status = getIntent().getStringExtra("status");
        titleET=findViewById(R.id.editText3);
        contentET=findViewById(R.id.editText2);
        time=findViewById(R.id.time);
        index=getIntent().getIntExtra("index",-1);
        if(status.equals("saved note")) {
            note = NoteModel.noteList.get(index);
            Toast.makeText(this, note.getTitle(),
                             Toast.LENGTH_SHORT).show();
        }else if(status.equals("new note")){
            note=new Note();
        }
        time.setText(note.getCurTimestr());
        savedTitle=note.getTitle();
        savedContent=note.getContent();
        titleET.setText(savedTitle);
        contentET.setText(savedContent);
        //Toast.makeText(this, status+index,
       //         Toast.LENGTH_SHORT).show();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Item1:
                        new MaterialAlertDialogBuilder(EditActivity.this)
                                .setTitle("删除")
                                .setMessage("将删除此页")

                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NoteModel.noteList.remove(note);
                                        MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                                        MainActivity.noteBaseRecyclerAdapter.notifyItemRemoved(index);
                                        new File(note.getLoc()).delete();
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        return true;
                    case R.id.Item0:
                        titleET.setText("");
                        contentET.setText("");
                        return true;
                    }


                return false;
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
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
        final Date curTime=new Date(System.currentTimeMillis());
        //final String loc=note.getLoc();
        if(status.equals("new note")){
            if(title.equals("")&&content.equals(""))return;
            else {
                note.setTitle(title);
                note.setCurTime(curTime);
                note.setContent(content);
                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();
                NoteModel.noteList.add(note);
                NoteModel.sortByLastEditTime();
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemChanged(index);
                saveNote(title,note.getCurTimestr(),content);

            }
        }else if(status.equals("saved note")){
            if(title.equals("")&&content.equals("")) {
                NoteModel.noteList.remove(note);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemRemoved(index);
                new File(note.getLoc()).delete();
                Toast.makeText(this, "已删除空白",
                                      Toast.LENGTH_SHORT).show();
            }
            else if(!title.equals(savedTitle)||!content.equals(savedContent)){
                note.setTitle(title);
                note.setContent(content);
                note.setCurTime(curTime);
                NoteModel.sortByLastEditTime();
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
                //MainActivity.noteBaseRecyclerAdapter.notifyItemChanged(index);
                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();
                saveNote(title,note.getCurTimestr(),content);

            }
        }


    }

    private void saveNote(String title,String time,String content){
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(note.getLoc(), Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(title+"\n");
            writer.write(time+"\n");
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


