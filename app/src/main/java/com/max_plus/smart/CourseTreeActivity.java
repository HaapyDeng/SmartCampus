package com.max_plus.smart;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

public class CourseTreeActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {
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
                String courseId;
//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/test2.mp4");
//                VideoView videoView = (VideoView) findViewById(R.id.video_view);
//                videoView.setMediaController(new MediaController(CourseTreeActivity.this));
//                videoView.setVideoURI(uri);
//                videoView.start();
//                videoView.requestFocus();

//                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/test2.mp4");
//                //调用系统自带的播放器
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Log.v("URI:::::::::", uri.toString());
//                intent.setDataAndType(uri, "video/mp4");
//                startActivity(intent);
                surfaceView = (SurfaceView) findViewById(R.id.video_surface);
                //给SurfaceView添加CallBack监听
                holder = surfaceView.getHolder();
                holder.addCallback(CourseTreeActivity.this);
                //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                //下面开始实例化MediaPlayer对象
                player = new MediaPlayer();
                player.setOnCompletionListener(CourseTreeActivity.this);
                player.setOnErrorListener(CourseTreeActivity.this);
                player.setOnInfoListener(CourseTreeActivity.this);
                player.setOnPreparedListener(CourseTreeActivity.this);
                player.setOnSeekCompleteListener(CourseTreeActivity.this);
                player.setOnVideoSizeChangedListener(CourseTreeActivity.this);
                Log.v("Begin:::", "surfaceDestroyed called");
                //然后指定需要播放文件的路径，初始化MediaPlayer
//                String dataPath = Environment.getExternalStorageDirectory().getPath() + "/test2.mp4";
                String dataPath = "/storage/sdcard/test2.mp4";
                Log.d("dataPath:>", dataPath);
                try {
                    try {
                        player.setDataSource(dataPath);

                        Log.v("Next:::", "surfaceDestroyed called");
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //然后，我们取得当前Display对象
                currDisplay = getWindowManager().getDefaultDisplay();
                return true;
            }
        });

    }

    private void initData() {
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        lData = new ArrayList<Item>();
        for (int i = 0; i < 10; i++) {
            lData.add(new Item("Unit" + " " + i, "" + (i + 10)));
        }
        iData.add(lData);
        gData.add(new Group("English", "1"));
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // 当MediaPlayer播放完成后触发
        Log.v("Play Over:::", "onComletion called");
        this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.v("Play Error:::", "onError called");
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        // 当一些特定信息出现或者警告时触发
        switch (i) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
// 当prepare完成后，该方法触发，在这里我们播放视频

        //首先取得video的宽和高
        vWidth = player.getVideoWidth();
        vHeight = player.getVideoHeight();

        if (vWidth > currDisplay.getWidth() || vHeight > currDisplay.getHeight()) {
            //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
            float wRatio = (float) vWidth / (float) currDisplay.getWidth();
            float hRatio = (float) vHeight / (float) currDisplay.getHeight();

            //选择大的一个进行缩放
            float ratio = Math.max(wRatio, hRatio);

            vWidth = (int) Math.ceil((float) vWidth / ratio);
            vHeight = (int) Math.ceil((float) vHeight / ratio);

            //设置surfaceView的布局参数
            surfaceView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));

            //然后开始播放视频

            player.start();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        // seek操作完成时触发
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        // 当video大小改变时触发
        //这个方法在设置player的source后至少触发一次
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 当SurfaceView中的Surface被创建的时候被调用
        //在这里我们指定MediaPlayer在当前的Surface中进行播放
        player.setDisplay(holder);
        //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
        player.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 当Surface尺寸等参数改变时触发
        Log.v("Surface Change:::", "surfaceChanged called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v("Surface Destory:::", "surfaceDestroyed called");
    }
}
