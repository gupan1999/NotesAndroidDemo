package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static com.example.myapplication1.MainActivity.searchView;

public class NewNoteActivity extends AppCompatActivity {
    private EditText titleET;
    private EditText contentET;
    private Note note;
    private String savedTitle;
    private String savedContent;
    private TextView time;
    private Toolbar toolbar;
    private boolean saved=false;
    private boolean delete=false;
    private int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_note);
        toolbar=findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        titleET=findViewById(R.id.editText3);
        contentET=findViewById(R.id.editText2);
        time=findViewById(R.id.time);
        note=new Note();
        time.setText(note.getCurTimestr());
        savedTitle=note.getTitle();
        savedContent=note.getContent();
        titleET.setText(savedTitle);
        contentET.setText(savedContent);
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
                    case R.id.Item0:
                        titleET.setText("");
                        contentET.setText("");
                        return true;
                    case R.id.Item2:
                        if(titleET.getText().toString().equals("")&&contentET.getText().toString().equals("")){
                            saved=true;
                            finish();
                        }else{
                            save();
                        }
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(!saved){
            if(contentET.getText().toString().equals("")&&titleET.getText().toString().equals(""));
            else save();
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        cnt++;
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
        if(!saved){
            if(contentET.getText().toString().equals("")&&titleET.getText().toString().equals(""));
            else save();
        }
        if(delete)deleteNote();
        if(MainActivity.searching)MainActivity.onQueryTextListener.onQueryTextChange(searchView.getQuery().toString());
        super.onDestroy();
    }

    public void save() {
        final String title =this.titleET.getText().toString() ;
        final String content=this.contentET.getText().toString();
        final Date curTime=new Date(System.currentTimeMillis());
        if(title.equals("")&&content.equals("")){
            delete=true;
            saved = true;
        }
        else if(!title.equals(savedTitle)||!content.equals(savedContent)){
            saved=true;
            note.setTitle(title);
            note.setCurTime(curTime);
            note.setContent(content);
            Toast.makeText(this, "已保存",
                    Toast.LENGTH_SHORT).show();
            if(cnt==0) NoteModel.noteList.add(note);
            NoteModel.sortByLastEditTime(NoteModel.noteList);
            MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
            saveNote();
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

    private void deleteNote(){
        NoteModel.noteList.remove(note);

        MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
        //File file =new File(note.getLoc());

        Log.d("loctest","file:"+note.getLoc());
        boolean result= getDir(note.getLoc(),MODE_PRIVATE).delete();
        if(result) {
            Toast.makeText(this, "删除成功",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(NewNoteActivity.this, "删除文件失败",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
