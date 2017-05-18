package com.max_plus.smart;

/**
 * Created by Administrator on 2017/5/18.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener {
    private final ArrayList<UserBean> data;

    public MyAdapter(ArrayList<UserBean> data) {
        this.data = data;
    }

    private OnItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText(data.get(position).getName());
        holder.tv_state.setText(data.get(position).getState());
        holder.iv_img.setBackgroundResource(data.get(position).getImg());
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        return data.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_state;
        public ImageView iv_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_state = (TextView) itemView.findViewById(R.id.tv_state);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }
}
