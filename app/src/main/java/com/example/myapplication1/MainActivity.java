package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    public static BaseRecyclerAdapter<Note>noteBaseRecyclerAdapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.recyclerview);
        floatingActionButton=findViewById(R.id.floatingActionButton);
        loadAll();
        noteBaseRecyclerAdapter=new BaseRecyclerAdapter<Note>(this,R.layout.item,NoteModel.noteList) {
            @Override
            public void convert(BaseViewHolder holder, Note note) {
                holder.setText(R.id.notetitle,note.getTitle());
                holder.setText(R.id.notecontent,NoteModel.cutTextShowed(note.getContent()));
            }

            @Override
            public void setting(final BaseViewHolder holder) {
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,EditActivity.class);
                        intent.putExtra("index",holder.getAdapterPosition());
                        //intent.putExtra("note",noteBaseRecyclerAdapter.getmDataByPosition(holder.getAdapterPosition()));
                        intent.putExtra("status","saved note");
                        startActivity(intent);
                    }

                });
                holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {                       //设置长按监听，只为获取选中的ViewHolder的位置
                        noteBaseRecyclerAdapter.setPosition(holder.getAdapterPosition());
                        return false;
                    }
                });
                holder.setOnCreateContextMenuListener(holder.getItemView());
            }
        };
        recyclerView.setAdapter(noteBaseRecyclerAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SpacesItemDecoration decoration = new SpacesItemDecoration(12);
        recyclerView.addItemDecoration(decoration);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,EditActivity.class);
                //intent.putExtra("cnt",NoteModel.noteList.size()-1);
                intent.putExtra("status","new note");
                //intent.putExtra("note",note);
                intent.putExtra("index",NoteModel.noteList.size()-1);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.remove){
            NoteModel.noteList.remove(noteBaseRecyclerAdapter.getmDataByPosition(noteBaseRecyclerAdapter.getPosition()));
            noteBaseRecyclerAdapter.notifyItemRemoved(noteBaseRecyclerAdapter.getPosition());  //移除item
            noteBaseRecyclerAdapter.notifyItemRangeChanged(noteBaseRecyclerAdapter.getPosition(),noteBaseRecyclerAdapter.getItemCount());  //正确删除后的动画效果
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    protected void onResume() {
        Log.d("notify","onResume");

        super.onResume();
    }

    @Override
    protected void onStop() {
        saveAll();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        NoteModel.noteList.clear();
        super.onDestroy();
    }

    private void saveAll(){
            SharedPreferences.Editor editor = getSharedPreferences("data",
                MODE_PRIVATE).edit();
            editor.putLong("num",Note.getCnt());
            editor.apply();
            FileOutputStream out;
            BufferedWriter writer = null;
            try {
                out = openFileOutput("locs", Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                for(Note note:NoteModel.noteList) {
                    writer.write(note.getLoc() + "\n");
                }
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
            Log.d("get_status","saveloc ok");

    }
    private void loadAll(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        Note.setCnt(pref.getLong("num",-1L));
            FileInputStream in;
            BufferedReader reader = null;
            try {
                in = openFileInput("locs");
                reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    Note note=new Note();
                    note.setLoc(line);
                    NoteModel.noteList.add(note);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        loadNotes();
        Log.d("get_status","load all ok");
    }

    private void loadNotes(){
        for(Note note:NoteModel.noteList) {
            FileInputStream in;
            BufferedReader reader = null;
            StringBuilder content = new StringBuilder();
            try {
                in = openFileInput(note.getLoc());
                reader = new BufferedReader(new InputStreamReader(in));
                note.setTitle(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                note.setContent(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

