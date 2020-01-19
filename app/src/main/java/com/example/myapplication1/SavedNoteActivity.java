package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static com.example.myapplication1.MainActivity.searchView;

public class SavedNoteActivity extends AppCompatActivity {
    private EditText titleET;
    private EditText contentET;
    private Note note;
    private String savedTitle;
    private String savedContent;
    private int index;
    private TextView time;
    private Toolbar toolbar;
    private boolean saved=false;
    private boolean delete=false;

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
        index=getIntent().getIntExtra("index",-1);
        note = NoteModel.curList.get(index);
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
                            delete = true;
                            finish();
                        }else{
                            KeyBoardUtil.hideKeyboard(SavedNoteActivity.this);
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
        if (!saved) save();
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
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
        if(!saved) save();
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
            NoteModel.sortByLastEditTime(NoteModel.noteList);
            NoteModel.sortByLastEditTime(NoteModel.curList);
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
        NoteModel.curList.remove(note);
        MainActivity.noteBaseRecyclerAdapter.notifyDataSetChanged();
        boolean result= getDir(note.getLoc(),MODE_PRIVATE).delete();
        if(result) {
            Toast.makeText(this, "删除成功",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(SavedNoteActivity.this, "删除文件失败",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
