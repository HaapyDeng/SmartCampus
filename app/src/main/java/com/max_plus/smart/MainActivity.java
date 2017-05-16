package com.max_plus.smart;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<UserBean> data = new ArrayList<UserBean>();
    public static String wsUrl = "ws://192.168.1.112:9502";
    private int state = 0;
    private TextView tv_english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        tv_english = (TextView) findViewById(R.id.tv_e);
        tv_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, TreeDataActivity.class);
//                startActivity(intent);
            }
        });
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    initView();
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
                    Toast.makeText(MainActivity.this, "I am running!!!!!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };


    private void initData() {
//        for (int i = 0; i < 49; i++) {
//            UserBean user = new UserBean();
//            user.setName("学生" + (i + 1));
//            user.setState(getString(R.string.absence));
//            user.setImg(R.drawable.img_absenteeism);
//            data.add(user);
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
                        System.out.println("I got a string: " + s);
                        //需要数据传递，用下面方法；
                        mHandler.sendEmptyMessage(4);
                        Message msg1 = new Message();
                        msg1.obj = "";//可以是基本类型，可以是对象，可以是List、map等；
                        mHandler.sendMessage(msg1);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("status")) {
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
//                                if (jsonObject.getJSONObject("data").has("error")) {
//                                    mHandler.sendEmptyMessage(3);
//
//                                    //需要数据传递，用下面方法；
//                                    Message msg = new Message();
//                                    msg.obj = jsonObject.getJSONObject("data").getString("error");//可以是基本类型，可以是对象，可以是List、map等；
//                                    mHandler.sendMessage(msg);
//                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (data.size() != 0) {
                                    data.clear();
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    UserBean user = new UserBean();
                                    JSONObject jsondata = jsonArray.getJSONObject(i);
                                    user.setName(jsondata.getString("username"));
                                    state = jsondata.getInt("state");
                                    switch (state) {
                                        case 0:
                                            user.setState(getString(R.string.absence));
                                            user.setImg(R.drawable.img_absenteeism);
                                            break;

                                        case 1:
                                            user.setState(getString(R.string.normal));
                                            user.setImg(R.drawable.img_normal);
                                            break;
                                        case 2:
                                            user.setState(getString(R.string.late));
                                            user.setImg(R.drawable.img_late);
                                            break;
                                        default:
                                            user.setState(getString(R.string.absence));
                                            user.setImg(R.drawable.img_absenteeism);
                                    }

                                    data.add(user);
                                }

                                mHandler.sendEmptyMessage(0);

                                Message msg = new Message();
                                msg.obj = "";//可以是基本类型，可以是对象，可以是List、map等；
                                mHandler.sendMessage(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.d("websocket连接失败", ex.toString());
//                        initData();
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(this, 7);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置固定大小
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, 7, GridLayoutManager.VERTICAL, false));
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
}
