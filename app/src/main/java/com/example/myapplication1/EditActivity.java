package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
    private boolean needsave=false;
    private boolean saved=false;
    private boolean delete=false;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle","onCreate");
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
        contentET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //EditActivity.this.toolbar.getMenu().removeItem(R.id.Item1);
                EditActivity.this.toolbar.getMenu().removeItem(R.id.Item0);
                EditActivity.this.toolbar.inflateMenu(R.menu.menu3);
            }
        });
        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };
        titleET.addTextChangedListener(textWatcher);
        contentET.addTextChangedListener(textWatcher);
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
                    /*
                    case R.id.Item1:
                        new MaterialAlertDialogBuilder(EditActivity.this)
                                .setTitle("删除")
                                .setMessage("将删除此页")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(status.equals("saved note")) {
                                            delete = true;
                                    }

                                        finish();
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                        return true;
                        */

                    case R.id.Item0:
                        titleET.setText("");
                        contentET.setText("");
                        return true;
                    case R.id.Item2:
                         if(EditActivity.this.titleET.getText().toString().equals("")&&EditActivity.this.titleET.getText().toString().equals(""))
                             delete=true;
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("Lifecylce","onSaveInstanceState");
        if (!saved) save();


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.d("Lifecycle","onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("Lifecycle","onRestart");
        if(saved)status="saved note";
        saved=false;
        savedTitle=note.getTitle();
        savedContent=note.getContent();
        super.onRestart();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d("Lifecycle","onDestroy");
        if(!saved) save();
        if(delete)deleteNote();
        super.onDestroy();
    }

    public void save() {

        final String title =this.titleET.getText().toString() ;
        final String content=this.contentET.getText().toString();
        final Date curTime=new Date(System.currentTimeMillis());
        //final String loc=note.getLoc();
        if(status.equals("new note")){
            if(title.equals("")&&content.equals(""))return;
            else if(!title.equals(savedTitle)||!content.equals(savedContent)){
                saved=true;
                note.setTitle(title);
                note.setCurTime(curTime);
                note.setContent(content);
                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();

                NoteModel.noteList.add(note);
                NoteModel.sortByLastEditTime(NoteModel.noteList);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();

                saveNote();

            }
        }else if(status.equals("saved note")){
            if(title.equals("")&&content.equals("")) {
                delete=true;
                saved = true;
                }
            else if(!title.equals(savedTitle)||!content.equals(savedContent)){
                saved = true;
                note.setTitle(title);
                note.setContent(content);
                note.setCurTime(curTime);
                NoteModel.sortByLastEditTime(NoteModel.noteList);
                MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();

                Toast.makeText(this, "已保存",
                        Toast.LENGTH_SHORT).show();
                saveNote();

            }
        }


    }
    private void deleteNote(){
        NoteModel.noteList.remove(note);
        MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
        File file =new File(note.getLoc());
        Log.d("loctest","file:"+note.getLoc());
        boolean result=file.delete();
        if(result) {
            Toast.makeText(this, "删除成功",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(EditActivity.this, "删除文件失败",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void saveNote(){
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(note.getLoc(), Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(note.getTitle()+"\n");
            writer.write(note.getCurTimestr()+"\n");
            writer.write(note.getContent());
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


