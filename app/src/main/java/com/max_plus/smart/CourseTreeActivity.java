package com.max_plus.smart;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.max_plus.smart.player.StartVideoActivity;

import java.util.ArrayList;

public class CourseTreeActivity extends Activity {
    private ExpandableListView exlist_text;
    private MyBaseExpandableListAdapter myAdapter = null;
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;
    private ArrayList<Item> lData = null;


    private Display currDisplay;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private MediaPlayer player;
    private int vWidth, vHeight;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_tree);
        initData();
        exlist_text = (ExpandableListView) findViewById(R.id.ev_list);
        exlist_text.setGroupIndicator(null);
        myAdapter = new MyBaseExpandableListAdapter(gData, iData, CourseTreeActivity.this);
        exlist_text.setAdapter(myAdapter);
        //为列表设置点击事件
        exlist_text.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (gData.get(groupPosition).getgId().equals("1")) {
                    Intent intent = new Intent();
                    intent.setClass(CourseTreeActivity.this, StartVideoActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent2 = new Intent();
                    intent2.setClass(CourseTreeActivity.this, HomeWorkActivity.class);
                    startActivity(intent2);
                }
                return true;
            }
        });

    }


    private void initData() {
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        lData = new ArrayList<Item>();
        for (int j = 1; j < 3; j++) {
//            iData.get(j).add(lData);
            if (j == 1) {
                iData.add(lData);
                gData.add(new Group("英语课程微视频", "1"));
            }
            if (j == 2) {
//                for (int i = 1; i < 10; i++) {
//                    lData.add(new Item("Unit" + " " + i, "" + (i + 10)));
//                }
                lData.add(new Item("Unit 1", "1"));
                lData.add(new Item("Unit 2", "2"));
                lData.add(new Item("Unit 3", "3"));
                lData.add(new Item("Unit 4", "4"));
                lData.add(new Item("Unit 5", "5"));
                iData.add(lData);
                iData.add(lData);
                gData.add(new Group("英语课程作业安排", "2"));
            }
        }
    }
}
