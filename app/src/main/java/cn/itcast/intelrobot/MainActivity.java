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
    private List<ChatBean> chatBeanList; // 存放所有聊天数据的集合
    private EditText et_send_msg;
    private Button btn_send;
    private static final String WEB_SITE = "http://192.168.234.1:3000"; // 修改为您的服务器地址
    private String sendMsg;    // 用户输入的消息
    private MHandler mHandler;
    public static final int MSG_OK = 1; // 获取数据成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatBeanList = new ArrayList<>();
        mHandler = new MHandler();

        initView(); // 初始化界面控件
    }

    public void initView() {
        rv_list = findViewById(R.id.rv_list);
        et_send_msg = findViewById(R.id.et_send_msg);
        btn_send = findViewById(R.id.btn_send);
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(this);
        adapter.setData(chatBeanList);
        rv_list.setAdapter(adapter);

        btn_send.setOnClickListener(v -> sendData());
    }

    private void sendData() {
        sendMsg = et_send_msg.getText().toString(); // 获取用户输入的消息
        if (TextUtils.isEmpty(sendMsg)) {
            Toast.makeText(this, "您还未输入任何信息", Toast.LENGTH_LONG).show();
            return;
        }
        et_send_msg.setText("");

        // 显示用户发送的消息
        showData(sendMsg, ChatBean.SEND);

        // 向服务器发送数据
        getDataFromServer();
    }

    private void showData(String message, int type) {
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(type); // type 表示消息类型
        chatBeanList.add(chatBean);

        adapter.notifyDataSetChanged();
    }

    private JSONObject transJson(String text) {
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
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject reqJson = transJson(sendMsg); // 构造 JSON 请求
        RequestBody requestBody = RequestBody.Companion.create(reqJson.toString(), JSON);
        Request request = new Request.Builder().url(WEB_SITE).post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.e("MainActivity", "res==" + res);
                Message msg = new Message();
                msg.what = MSG_OK;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "请求失败：" + e.getMessage());
                runOnUiThread(() -> showData("网络请求失败，请稍后重试", ChatBean.RECEIVE));
            }
        });
    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == MSG_OK && msg.obj != null) {
                String response = (String) msg.obj;
                parseData(response);
            }
        }
    }

    private void parseData(String jsonData) {
        try {
            JSONObject obj = new JSONObject(jsonData);
            JSONArray results = obj.getJSONArray("results");
            JSONObject values = results.getJSONObject(0).getJSONObject("values");
            String content = values.getString("text"); // 获取服务器返回的消息
            showData(content, ChatBean.RECEIVE);
        } catch (JSONException e) {
            e.printStackTrace();
            showData("解析数据失败", ChatBean.RECEIVE);
        }
    }
}
