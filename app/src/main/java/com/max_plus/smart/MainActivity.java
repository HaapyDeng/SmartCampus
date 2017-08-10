package com.max_plus.smart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<UserBean> data = new ArrayList<UserBean>();
    public String wsUrl = "";
    //    public static String wsUrl = "ws://192.168.1.116:9502";
    private int state = 0, id = 0;
    private MyAdapter mAdapter;
    private TextView tv_english;
    private int lateCount = 0, normalCount = 0, absenceCount = 0;
    private TextView total, late, absence, tv_nomal, time_hour, time_year;
    private int tag = 5, totalCount = 0;
    private Button btn_count;
    private ImageView head_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (wsUrl.equals("")) {
            final EditText et = new EditText(this);
            new AlertDialog.Builder(MainActivity.this).setTitle("配置服务器地址:")
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            wsUrl = "ws://" + input;
                            initData();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            initData();
        }
        initView();
        //显示统计人数
        total = (TextView) findViewById(R.id.tv_total);
        late = (TextView) findViewById(R.id.tv_late);
        absence = (TextView) findViewById(R.id.tv_absenceCount);
        tv_nomal = (TextView) findViewById(R.id.tv_nomal);
        btn_count = (Button) findViewById(R.id.btn_count);
        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CountActivity.class);
                startActivity(intent);
            }
        });
        total.setText("" + totalCount);
        late.setText("" + lateCount);
        absence.setText("" + absenceCount);
        tv_nomal.setText("" + normalCount);
        //点击课程跳转
        tv_english = (TextView) findViewById(R.id.tv_e);
        tv_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CourseTreeActivity.class);
                startActivity(intent);
            }
        });
        //时间显示
        time_hour = (TextView) findViewById(R.id.tv_time_hour);
        time_year = (TextView) findViewById(R.id.tv_time_year);
        //启动时间线程
        new TimeThread().start();
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 6;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    initView();
                    total = (TextView) findViewById(R.id.tv_total);
                    late = (TextView) findViewById(R.id.tv_late);
                    absence = (TextView) findViewById(R.id.tv_absenceCount);
                    total.setText("" + totalCount);
                    late.setText("" + lateCount);
                    tv_nomal.setText("" + normalCount);
                    absence.setText("" + (totalCount - lateCount - normalCount));
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "连接服务器失败，请重启设备", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "连接服务器失败，请重启设备", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
//                    Toast.makeText(MainActivity.this, "I am running!!!!!", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    total = (TextView) findViewById(R.id.tv_total);
                    late = (TextView) findViewById(R.id.tv_late);
                    absence = (TextView) findViewById(R.id.tv_absenceCount);
                    total.setText("" + totalCount);
                    late.setText("" + lateCount);
                    tv_nomal.setText("" + normalCount);
                    absence.setText("" + (totalCount - lateCount - normalCount));
                    initView();
                    break;
                case 6:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
                    time_hour.setText(format1.format(date));
                    time_year.setText(format2.format(date));
                    break;
                default:
                    break;
            }
        }

    };

    //测试模拟数据
    private void initData2() {
        for (int i = 0; i < 49; i++) {
            UserBean user = new UserBean();
            user.setName("学生" + (i + 1));
            user.setState(getString(R.string.absence));
            user.setImg(R.drawable.img_absenteeism);
            data.add(user);
            totalCount = totalCount + 1;
            absenceCount = absenceCount + 1;
        }
    }

    private void initData() {
//        Toast.makeText(this, "wsUrl==>>" + wsUrl, Toast.LENGTH_SHORT).show();
        AsyncHttpClient.getDefaultInstance().websocket(wsUrl, "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, final WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }
                        Log.d("mac==>>", jsonMacData());
                        webSocket.send(jsonMacData());
                        webSocket.setStringCallback(new WebSocket.StringCallback() {
                                                        public void onStringAvailable(String s) {
                                                            totalCount = 0;
                                                            absenceCount = 0;
                                                            normalCount = 0;
                                                            lateCount = 0;
                                                            System.out.println("I got a string: " + s);

                                                            try {
                                                                System.out.println("I got a ffffffffffffffffff: ");
                                                                JSONObject jsonObject = new JSONObject(s);
                                                                if (jsonObject.has("status")) {
                                                                    System.out.println("I got a fffqqqqqqqqqf: ");
                                                                    if (jsonObject.get("status").equals(1)) {
                                                                        mHandler.sendEmptyMessage(1);

                                                                        //需要数据传递，用下面方法；
                                                                        Message msg = new Message();
                                                                        msg.obj = "";//可以是基本类型，可以是对象，可以是List、map等；
                                                                        mHandler.sendMessage(msg);
                                                                    } else {
                                                                        mHandler.sendEmptyMessage(2);

                                                                        //需要数据传递，用下面方法；
                                                                        Message msg = new Message();
                                                                        msg.obj = "";//可以是基本类型，可以是对象，可以是List、map等；
                                                                        mHandler.sendMessage(msg);
                                                                    }
                                                                }
                                                                if (jsonObject.has("data")) {
                                                                    System.out.println("I got a fddddddddddddddddddf: ");
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    if (data.size() != 0) {
                                                                        System.out.println("I got a zzzzzzzzzzzzqf: ");
                                                                        data.clear();
                                                                    }
                                                                    System.out.println("I got a zzzzzzzzzz111111111111zzqf: ");
                                                                    totalCount = jsonArray.length();
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        UserBean user = new UserBean();
                                                                        JSONObject jsondata = jsonArray.getJSONObject(i);
                                                                        state = jsondata.getInt("state");
                                                                        System.out.println("id==>>>:" + id);
                                                                        id = jsondata.getInt("id");
                                                                        SharedPreferences pref1 = getSharedPreferences("tag1", Activity.MODE_PRIVATE);
                                                                        String tag1 = pref1.getString("tag1" + i, "");
                                                                        int id1 = 0;
                                                                        id1 = pref1.getInt("id1" + i, 0);
                                                                        System.out.println("I got a eeeeeeeeeee: " + tag1 + ":" + id1);
                                                                        SharedPreferences pref2 = getSharedPreferences("tag2", Activity.MODE_PRIVATE);
                                                                        String tag2 = pref2.getString("tag2" + i, "");
                                                                        int id2 = 0;
                                                                        id2 = pref2.getInt("id2" + i, 0);
                                                                        System.out.println("I got a eeeeeeeeeee2222: " + tag2 + ":" + id2);
                                                                        SharedPreferences pref3 = getSharedPreferences("tag3", Activity.MODE_PRIVATE);
                                                                        String tag3 = pref3.getString("tag3" + i, "");
                                                                        int id3 = 0;
                                                                        id3 = pref3.getInt("id3" + i, 0);
                                                                        System.out.println("I got a eeeeeeeeeee33333333: " + tag3 + ":" + id3);
                                                                        if (id == id1) {
                                                                            user.setName(jsondata.getString("username"));
                                                                            user.setState(getString(R.string.absence));
                                                                            user.setImg(R.drawable.img_absenteeism);
                                                                            user.setTag(1);
                                                                            user.setId(id1);
                                                                            absenceCount = absenceCount + 1;
                                                                        } else if (id == id2) {
                                                                            user.setName(jsondata.getString("username"));
                                                                            user.setState(getString(R.string.normal));
                                                                            user.setImg(R.drawable.img_normal);
                                                                            user.setTag(2);
                                                                            user.setId(id2);
                                                                            normalCount = normalCount + 1;
                                                                        } else if (id == id3) {
                                                                            user.setName(jsondata.getString("username"));
                                                                            user.setState(getString(R.string.late));
                                                                            user.setImg(R.drawable.img_late);
                                                                            user.setTag(3);
                                                                            user.setId(id3);
                                                                            lateCount = lateCount + 1;
                                                                        } else {
                                                                            Log.d("running this......", "");
                                                                            switch (state) {
                                                                                case 0:
                                                                                    user.setName(jsondata.getString("username"));
                                                                                    user.setTag(5);
                                                                                    user.setId(jsondata.getInt("id"));
                                                                                    user.setState(getString(R.string.absence));
                                                                                    user.setImg(R.drawable.img_absenteeism);
                                                                                    absenceCount = absenceCount + 1;
                                                                                    break;

                                                                                case 1:
                                                                                    user.setName(jsondata.getString("username"));
                                                                                    user.setTag(5);
                                                                                    user.setId(jsondata.getInt("id"));
                                                                                    user.setState(getString(R.string.normal));
                                                                                    user.setImg(R.drawable.img_normal);
                                                                                    normalCount = normalCount + 1;
                                                                                    break;
                                                                                case 2:
                                                                                    user.setName(jsondata.getString("username"));
                                                                                    user.setTag(5);
                                                                                    user.setId(jsondata.getInt("id"));
                                                                                    user.setState(getString(R.string.late));
                                                                                    user.setImg(R.drawable.img_late);
                                                                                    lateCount = lateCount + 1;
                                                                                    break;
                                                                                default:
                                                                                    break;
                                                                            }
                                                                        }

                                                                        data.add(user);
                                                                    }
                                                                }


                                                                mHandler.sendEmptyMessage(0);
                                                                Message msg = new Message();
                                                                msg.obj = "";//可以是基本类型，可以是对象，可以是List、map等；
                                                                mHandler.sendMessage(msg);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                        );
                        webSocket.setClosedCallback(new
                                                            CompletedCallback() {
                                                                @Override
                                                                public void onCompleted(Exception ex) {
                                                                    Log.d("websocket连接失败", ex.toString());
//                        initData();
                                                                }
                                                            }

                        );
                        webSocket.setDataCallback(new

                                                          DataCallback() {
                                                              public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                                                                  System.out.println("I got some bytes!");
                                                                  byteBufferList.recycle();
                                                              }
                                                          }

                        );
                    }
                }

        );
    }


    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(this, 7);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置固定大小
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, 7, GridLayoutManager.VERTICAL, false));
//        mRecyclerView.setAdapter(new MyAdapter(data));
        mAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
//                Toast.makeText(MainActivity.this, data.get(position).getName(), Toast.LENGTH_LONG).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请选择修改状态");
                final String[] state = {"缺勤", "正常", "迟到"};
                final String[] chooseState = {"正常"};
                final int[] chooseId = {0};
                //    设置一个单项选择下拉框
                builder.setSingleChoiceItems(state, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chooseId[0] = which;
                        System.out.println(which);
//                        Toast.makeText(MainActivity.this, "状态为：" + state[which], Toast.LENGTH_SHORT).show();
                        chooseState[0] = state[which].toString();

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (chooseId[0] == 0) {
                            if (data.get(position).getState().equals(getString(R.string.normal))) {
                                absenceCount = absenceCount + 1;
                                normalCount = normalCount - 1;
                            } else if (data.get(position).getState().equals(getString(R.string.late))) {
                                absenceCount = absenceCount + 1;
                                lateCount = lateCount - 1;
                            } else if (data.get(position).getState().equals(getString(R.string.absence))) {
                                absenceCount = absenceCount;
                            }
                            data.get(position).setImg(R.drawable.img_absenteeism);
                            data.get(position).setState(getString(R.string.absence));
                            data.get(position).setTag(1);
                            data.get(position).setId(data.get(position).getId());
                            SharedPreferences pref = MainActivity.this.getSharedPreferences("tag1", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("id1" + position, data.get(position).getId());
                            System.out.println("id==>>>:::" + data.get(position).getId());
                            editor.putString("tag1" + position, "" + 1);
                            editor.commit();
                            //需要数据传递，用下面方法；
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        } else if (chooseId[0] == 1) {
                            if (data.get(position).getState().equals(getString(R.string.absence))) {
                                normalCount = normalCount + 1;
                                absenceCount = absenceCount - 1;
                            } else if (data.get(position).getState().equals(getString(R.string.late))) {
                                lateCount = lateCount + 1;
                                normalCount = normalCount - 1;
                            } else if (data.get(position).getState().equals(getString(R.string.normal))) {
                                absenceCount = absenceCount;
                            }
                            data.get(position).setImg(R.drawable.img_normal);
                            data.get(position).setState(getString(R.string.normal));
                            data.get(position).setTag(2);
                            SharedPreferences pref = MainActivity.this.getSharedPreferences("tag2", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("id2" + position, data.get(position).getId());
                            System.out.println("id2==>>>:::" + data.get(position).getId());
                            editor.putString("tag2" + position, "" + 2);
                            editor.commit();
//                            normalCount = normalCount + 1;
//                            totalCount = totalCount - 1;
                            //需要数据传递，用下面方法；
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        } else {
                            if (data.get(position).getState().equals(getString(R.string.absence))) {
                                lateCount = lateCount + 1;
                                absenceCount = absenceCount - 1;
                            } else if (data.get(position).getState().equals(getString(R.string.late))) {
                                lateCount = lateCount;
                            } else if (data.get(position).getState().equals(getString(R.string.normal))) {
                                lateCount = lateCount + 1;
                                normalCount = normalCount - 1;
                            }
                            data.get(position).setImg(R.drawable.img_late);
                            data.get(position).setState(getString(R.string.late));
                            data.get(position).setTag(3);
                            SharedPreferences pref = MainActivity.this.getSharedPreferences("tag3", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("id3" + position, data.get(position).getId());
                            System.out.println("id3==>>>:::" + data.get(position).getId());
                            editor.putString("tag3" + position, "" + 3);
                            editor.commit();
                            Log.d("修改的tag：：：", "" + data.get(position).getTag());
                            //需要数据传递，用下面方法；
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
//                            totalCount = totalCount - 1;
                        }


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();
            }
        });
    }


    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public String jsonMacData() {
        String jsonresult = "";//定义返回字符串
//        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            JSONObject jsonObj = new JSONObject();//pet对象，json形式
            jsonObj.put("mac", getLocalMacAddress());//向pet对象里面添加值
            // 把每个数据当作一对象添加到数组里
            jsonarray.put(jsonObj);//向json数组里面添加pet对象
//            object.put("data", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = jsonarray.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("生成的json串为:", jsonresult);
        return jsonresult;
    }

    //防止RecyclerView在刷新数据的时候会出现异常，导致崩溃
    public class WrapContentLinearLayoutManager extends GridLayoutManager {


        public WrapContentLinearLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            try {
                super.onLayoutCompleted(state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy==>>>:");
        SharedPreferences dataBase1 = getSharedPreferences("tag1", Activity.MODE_PRIVATE);
        SharedPreferences dataBase2 = getSharedPreferences("tag2", Activity.MODE_PRIVATE);
        SharedPreferences dataBase3 = getSharedPreferences("tag3", Activity.MODE_PRIVATE);
        dataBase1.edit().clear().commit();
        dataBase2.edit().clear().commit();
        dataBase3.edit().clear().commit();
        super.onDestroy();
    }
}
