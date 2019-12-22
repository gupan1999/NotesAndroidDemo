package com.example.myapplication1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    public static BaseRecyclerAdapter<Note>noteBaseRecyclerAdapter;
    private Toolbar toolbar;
    public static SearchView searchView;
    public static boolean searching=false;
    public static SearchView.OnQueryTextListener onQueryTextListener;
    SearchView.OnCloseListener onCloseListener;
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
                holder.setText(R.id.time2,note.getCurTimestr());
                if(!searching) holder.setText(R.id.notecontent,NoteModel.cutTextShowed(note.getContent(),0));
                else holder.setText(R.id.notecontent,NoteModel.cutTextShowed(note.getContent(),note.getSearchIndexContent()));
            }

            @Override
            public void setting(final BaseViewHolder holder) {
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(MainActivity.this,SavedNoteActivity.class);
                        intent.putExtra("index",holder.getAdapterPosition());
                        //intent.putExtra("note",noteBaseRecyclerAdapter.getmDataByPosition(holder.getAdapterPosition()));
                        //intent.putExtra("status","saved note");
                        startActivity(intent);
                        //SavedNoteFragment savedNoteFragment=new SavedNoteFragment(holder.getAdapterPosition());
                        //FragmentManager fragmentManager=getSupportFragmentManager();
                        //FragmentTransaction transaction = fragmentManager.beginTransaction();
                        //transaction.add(R.id.recyclerview,savedNoteFragment);
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
                Intent intent=new Intent(MainActivity.this,NewNoteActivity.class);
                //intent.putExtra("status","new note");
                intent.putExtra("index",NoteModel.noteList.size()-1);
                startActivity(intent);
            }
        });
        NoteModel.curList=NoteModel.noteList;
        onCloseListener=new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searching=false;
                return false;
            }
        };
        onQueryTextListener=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searching=false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searching=true;
                List<Note>tempList=new ArrayList<Note>();
                for(Note note:NoteModel.noteList){
                    String content=note.getContent();
                    String title=note.getTitle();
                    if(content.contains(newText)||title.contains(newText)){
                        if(title.contains(newText))note.setSearchIndexTitle(title.indexOf(newText));
                        if(content.contains(newText)) note.setSearchIndexContent(content.indexOf(newText));
                        tempList.add(note);
                    }
                }
                NoteModel.curList=tempList;
                NoteModel.sortByLastEditTime(NoteModel.curList);
                noteBaseRecyclerAdapter.setmData(NoteModel.curList);
                noteBaseRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        };
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.remove){
            Note note=noteBaseRecyclerAdapter.getmDataByPosition(noteBaseRecyclerAdapter.getPosition());
            NoteModel.noteList.remove(note);
            NoteModel.curList.remove(note);
            noteBaseRecyclerAdapter.notifyItemRemoved(noteBaseRecyclerAdapter.getPosition());  //移除item
            noteBaseRecyclerAdapter.notifyItemRangeChanged(noteBaseRecyclerAdapter.getPosition(),noteBaseRecyclerAdapter.getItemCount());  //正确删除后的动画效果
            boolean result= getDir(note.getLoc(),MODE_PRIVATE).delete();
            if(result) {
                Toast.makeText(this, "删除成功",
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "删除文件失败",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView=(SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {
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
                        Log.d("file","closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        loadNotes();
        Log.d("get_status","load all ok");
    }

    private void loadNotes (){
        for(Note note:NoteModel.noteList) {
            FileInputStream in;
            BufferedReader reader = null;
            StringBuilder content = new StringBuilder();
            try {
                in = openFileInput(note.getLoc());
                reader = new BufferedReader(new InputStreamReader(in));
                note.setTitle(reader.readLine());
                note.setCurTime(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null) {

                    content.append(line);
                    content.append("\n");
                }
                note.setContent(content.toString());
            } catch (Exception e) {
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

