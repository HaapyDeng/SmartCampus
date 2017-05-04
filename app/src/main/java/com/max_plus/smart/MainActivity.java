package com.max_plus.smart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<UserBean> data = new ArrayList<UserBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 49; i++) {
            UserBean user = new UserBean();
            user.setName("小明明");
            user.setState("缺勤");
            user.setImg(R.drawable.img_absenteeism);
            data.add(user);
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(this, 7);
        //设置固定大小
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(new Myadapter(data));
    }


    private class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder> {
        private final ArrayList<UserBean> data;

        public Myadapter(ArrayList<UserBean> data) {
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv_name.setText(data.get(position).getName());
            holder.tv_state.setText(data.get(position).getState());
            holder.iv_img.setBackgroundResource(data.get(position).getImg());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
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
}
