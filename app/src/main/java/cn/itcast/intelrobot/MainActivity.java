package cn.itcast.intelrobot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv_list;
    private ChatAdapter adapter;
    private List<ChatBean> chatBeanList; //存放所有聊天数据的集合
    private EditText et_send_msg;
    private Button btn_send;
    //接口地址
    private static final String WEB_SITE = "http://192.168.234.1:3000";
    private String sendMsg;    //发送的消息
    private String welcome[];  //存储欢迎消息
    private MHandler mHandler;
    public static final int MSG_OK = 1;//获取数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatBeanList = new ArrayList<ChatBean>();
        mHandler = new MHandler();
        //获取欢迎消息
        welcome = getResources().getStringArray(R.array.welcome);
        initView(); //初始化界面控件
    }
    public void initView() {
        rv_list = findViewById(R.id.rv_list);
        et_send_msg = findViewById(R.id.et_send_msg);
        btn_send = findViewById(R.id.btn_send);
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this);
        adapter.setData(chatBeanList);
        rv_list.setAdapter(adapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendData();//点击“发送”按钮，发送消息
            }
        });
        et_send_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() ==
                        KeyEvent.ACTION_DOWN) {
                    sendData();//点击Enter键也可以发送消息
                }
                return false;
            }
        });
        //获取一个随机数
        int position = (int) (Math.random() * welcome.length - 1);
        //用随机数获取机器人发送的欢迎消息
        showData(welcome[position],ChatBean.RECEIVE);
    }
    private void showData(String message,int type) {
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(type);             //type表示消息的类型
        chatBeanList.add(chatBean);         //将信息添加到chatBeanList集合中
        adapter.notifyDataSetChanged();
    }
    private void sendData() {
        sendMsg = et_send_msg.getText().toString(); //获取用户输入的信息
        if (TextUtils.isEmpty(sendMsg)) {             //判断是否为空
            Toast.makeText(this, "您还未输任何信息哦", Toast.LENGTH_LONG).show();
            return;
        }
        et_send_msg.setText("");
        //替换空格和换行
        sendMsg = sendMsg.replaceAll(" ", "").replaceAll("\n", "").trim();
        showData(sendMsg,ChatBean.SEND);
        getDataFromServer();              //从服务器获取机器人发送的消息
    }
    public JSONObject transJson(String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reqType", 0);
            JSONObject perception = new JSONObject();
            JSONObject inputText = new JSONObject();
            inputText.put("text", text);
            perception.put("inputText", inputText);
            jsonObject.put("perception", perception);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    private void getDataFromServer() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //请求的数据类型设置为JSON格式
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject reqJson = transJson(sendMsg); //请求的JSON数据
        RequestBody requestBody = RequestBody.Companion.create(String.valueOf(reqJson),JSON);
        Request request = new Request.Builder().url(WEB_SITE).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.e("MainActivity","res=="+res);
                Message msg = new Message();
                msg.what = MSG_OK;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }
            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
                        parseData(vlResult);//该方法在后续创建
                    }
                    break;
            }
        }
    }
    private void parseData(String JsonData) {
        try {
            JSONObject obj = new JSONObject(JsonData);
            JSONObject objIntent = obj.getJSONObject("intent");
            int code = objIntent.getInt("code");         //获取消息的状态码
            JSONArray arr = obj.getJSONArray("results");
            JSONObject objRes = arr.getJSONObject(0);
            JSONObject objText = objRes.getJSONObject("values");
            String content = objText.getString("text"); //获取机器人回复的消息
            updateView(code, content);                      //更新界面
        } catch (JSONException e) {
            e.printStackTrace();
            showData("主人，你的网络不好哦", ChatBean.RECEIVE);
        }
    }
    private void updateView(int code, String content) {
        switch (code) {
            case 4001:
                showData("主人，今天我累了，我要休息了，明天再来找我耍吧",ChatBean.RECEIVE);
                break;
            case 4002:
                showData("主人，你说的是外星语吗？",ChatBean.RECEIVE);
                break;
            case 4003:
                showData("主人，我今天要去约会哦，暂不接客啦",ChatBean.RECEIVE);
                break;
            case 4005:
                showData("主人，明天再和你耍啦，我生病了，呜呜......",ChatBean.RECEIVE);
                break;
            default:
                showData(content,ChatBean.RECEIVE);
                break;
        }
    }
    protected long exitTime;//记录第一次点击时的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出智能聊天程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
