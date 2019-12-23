package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    public static BaseRecyclerAdapter<Note>noteBaseRecyclerAdapter;
    private Toolbar toolbar;
    public static SearchView searchView;
    public static SearchView.OnQueryTextListener onQueryTextListener;
    private SearchView.OnCloseListener onCloseListener;
    public static boolean searching=false;

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

                holder.setText(R.id.time2,note.getCurTimestr());
                if(!searching){
                    holder.setText(R.id.notecontent,cutTextShowed(note.getContent(),0));
                    holder.setText(R.id.notetitle,note.getTitle());
                }else {
                    if (note.getSearchIndexTitle() != -1 && note.getSearchIndexContent() == -1) {
                        cutAndStrengthenText(note.getTitle(), searchView.getQuery().toString(), note.getSearchIndexTitle(), (TextView) holder.getView(R.id.notetitle));
                        holder.setText(R.id.notecontent, cutTextShowed(note.getContent(), 0));
                    }else if (note.getSearchIndexContent() != -1 && note.getSearchIndexTitle() == -1) {
                        cutAndStrengthenText(note.getContent(), searchView.getQuery().toString(), note.getSearchIndexContent(), (TextView) holder.getView(R.id.notecontent));
                        holder.setText(R.id.notetitle, note.getTitle());
                    }else if(note.getSearchIndexTitle() != -1 && note.getSearchIndexContent() != -1){
                        cutAndStrengthenText(note.getTitle(), searchView.getQuery().toString(), note.getSearchIndexTitle(), (TextView) holder.getView(R.id.notetitle));
                        cutAndStrengthenText(note.getContent(), searchView.getQuery().toString(), note.getSearchIndexContent(), (TextView) holder.getView(R.id.notecontent));
                    }else {
                        holder.setText(R.id.notecontent,cutTextShowed(note.getContent(),0));
                        holder.setText(R.id.notetitle,note.getTitle());
                    }

                }
                note.setSearchIndexContent(-1);
                note.setSearchIndexTitle(-1);
            }


            @Override
            public void setting(final BaseViewHolder holder) {
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,SavedNoteActivity.class);
                        intent.putExtra("index",holder.getAdapterPosition());
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
                startActivity( new Intent(MainActivity.this,NewNoteActivity.class));
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searching=true;
                List<Note>tempList=new ArrayList<Note>();
                if(newText.equals(""))tempList=NoteModel.noteList;
                else {
                    newText = newText.toLowerCase();
                    for (Note note : NoteModel.noteList) {
                        String content = note.getContent().toLowerCase();
                        String title = note.getTitle().toLowerCase();
                        if ( (content.contains(newText) || title.contains(newText))) {
                            if (title.contains(newText))
                                note.setSearchIndexTitle(title.indexOf(newText));
                            if (content.contains(newText))
                                note.setSearchIndexContent(content.indexOf(newText));
                            tempList.add(note);
                        }
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

    public static String cutTextShowed(String text,int begin) {
        int n=0;
        int newBegin=0;
        int newEnd=text.length();
        for(int i=1;i<text.length()-begin;i++){
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
    private void cutAndStrengthenText(String text,String key,int begin,TextView textView){
        int n=0;
        int newBegin=0;
        int newEnd=text.length();
        for(int i=1;i<text.length()-begin;i++){
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
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(text.substring(newBegin,newEnd));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.dodgerblue));
        ssbuilder.setSpan(span,begin-newBegin,begin-newBegin+key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssbuilder);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView=(SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    protected void onStop() {
        saveAll();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        NoteModel.noteList.clear();
        NoteModel.curList.clear();
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

    static class MyHandler extends Handler {
        WeakReference<AppCompatActivity > mActivityReference;
        MyHandler(AppCompatActivity activity) {
            mActivityReference= new WeakReference<AppCompatActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final AppCompatActivity activity = mActivityReference.get();
            if (activity != null) {

            }
        }
    }

}

