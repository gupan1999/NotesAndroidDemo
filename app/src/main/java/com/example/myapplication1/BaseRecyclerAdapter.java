package com.example.myapplication1;

import android.content.Context;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder>{
    private Context mContext;
    private int mLayoutId;
    private List<T> mData;
    private int position;    //用于记录被选中的ViewHolder的位置
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public T getmDataByPosition(int position){
        return mData.get(position);
    }

    public BaseRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public int getmLayoutId() {
        return mLayoutId;
    }

    public void setmLayoutId(int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    public List<T> getmData() {
        return mData;
    }

    public void setmData(List<T> mData) {
        this.mData = mData;
    }

    public BaseRecyclerAdapter(Context mContext, int mLayoutId, List<T> mData) {
        this.mContext = mContext;
        this.mLayoutId = mLayoutId;
        this.mData = mData;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder=BaseViewHolder.getRecyclerHolder(mContext, parent, mLayoutId);
        setting(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        convert(holder, mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData!=null?mData.size():0;
    }
    public void updateItems(List<T>newList){
        mData=newList;
    }




    /**
     * 对外提供的方法
     */
    public abstract void convert(BaseViewHolder holder, T t);

    public abstract  void setting(BaseViewHolder holder);

}