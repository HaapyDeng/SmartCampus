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
                Intent intent = new Intent();
                intent.setClass(CourseTreeActivity.this, StartVideoActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }


    private void initData() {
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        lData = new ArrayList<Item>();
        for (int i = 0; i < 5; i++) {
            lData.add(new Item("Unit" + " " + i, "" + (i + 10)));
        }
        iData.add(lData);
        gData.add(new Group("English", "1"));
    }

}
