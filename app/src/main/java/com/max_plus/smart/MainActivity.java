package com.max_plus.smart;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    public static String wsUrl = "ws://192.168.1.103:9502";
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    private void initData() {

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
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                UserBean user = new UserBean();
                                JSONObject jsondata = jsonArray.getJSONObject(i);
                                user.setName(jsondata.getString("username"));
                                state = jsondata.getInt("state");
                                switch (state) {
                                    case 0:
                                        user.setState(String.valueOf(R.string.absence));
                                        user.setImg(R.drawable.img_absenteeism);
                                        break;
                                    case 1:
                                        user.setState(String.valueOf(R.string.normal));
                                        user.setImg(R.drawable.img_normal);
                                        break;
                                    case 2:
                                        user.setState(String.valueOf(R.string.late));
                                        user.setImg(R.drawable.img_late);
                                        break;
                                    default:
                                        user.setState(String.valueOf(R.string.absence));
                                        user.setImg(R.drawable.img_absenteeism);
                                }
                                data.add(user);
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
                        initData();
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

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public String jsonMacData() {
        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            JSONObject jsonObj = new JSONObject();//pet对象，json形式
            jsonObj.put("mac", getLocalMacAddress());//向pet对象里面添加值
            // 把每个数据当作一对象添加到数组里
            jsonarray.put(jsonObj);//向json数组里面添加pet对象
            object.put("data", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("生成的json串为:", jsonresult);
        return jsonresult;
    }
}
