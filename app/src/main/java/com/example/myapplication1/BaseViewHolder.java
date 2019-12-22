package com.example.myapplication1;

import android.content.Context;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/6 0006.
 * huangjialin
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private SparseArray<View> mViews; //用来存储控件的键值对容器,如1:
    private View mConvertView; //每个子项的外层布局
    private Context mContext;


    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();

        //mConvertView.setOnCreateContextMenuListener(this);
    }


    /**
     * 提供一个获取ViewHolder的方法
     */
    public static BaseViewHolder getRecyclerHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BaseViewHolder(context, itemView);
    }


    /**
     * 获取控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    /**
     * 给TextView设置setText方法
     */
    public BaseViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    /**
     * 给ImageView设置setImageResource方法
     */
    public BaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 添加点击事件
     */
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public void setOnCreateContextMenuListener(View v){
        v.setOnCreateContextMenuListener(this);
    }


    public View getItemView(){
        return mConvertView;
    }

    @Override          //实现接口View.OnCreateContextMenuListener的回调方法，在创建ContextMenu时调用
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
      new MenuInflater(mContext).inflate(R.menu.context_menu,menu);    //动态加载menu/context_menu的布局
    }
}