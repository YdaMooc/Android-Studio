package cn.itcast.intelrobot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.intelrobot.pojo.IPResponse;
import cn.itcast.intelrobot.pojo.WeatherForecastResponse;
import cn.itcast.intelrobot.pojo.WeatherResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 主活动类，用于展示一个简单的智能聊天助手界面。
 * 用户可以输入天气查询、预报天气、IP定位指令，程序将通过高德地图API进行查询并在界面显示结果。
 */
public class MainActivity extends AppCompatActivity {

    // 聊天列表组件
    private RecyclerView rv_list;
    // 自定义的聊天信息适配器
    private ChatAdapter adapter;
    // 聊天信息的数据源
    private List<ChatBean> chatBeanList;
    // 用户输入文本框
    private EditText et_send_msg;
    // 发送按钮
    private Button btn_send;

    // 高德地图API密钥（请根据自己的密钥进行替换）
    private static final String AMAP_API_KEY = "0fff6d333a597852034f78dfaa8a0c00";
    // 高德地理编码API地址
    private static final String AMAP_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    // 高德天气查询API地址
    private static final String AMAP_WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    // 高德IP定位API地址
    private static final String AMAP_IP_URL = "https://restapi.amap.com/v3/ip";

    // 用户当前输入的消息内容
    private String sendMsg;
    // 默认回复内容（当无法理解用户的输入时）
    private static final String DEFAULT_REPLY = "抱歉，我无法理解您的问题，请输入“功能”查看。";

    // 欢迎信息和功能说明
    private static final String WELCOME_MESSAGE = "欢迎使用智能聊天助手！\n当前支持的功能：\n" +
            "1. 天气查询\n" +
            "2. 预报天气\n" +
            "3. IP定位";

    /**
     * 活动创建时调用，初始化界面布局和数据
     *
     * @param savedInstanceState 活动重建时的状态信息
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化聊天数据列表
        chatBeanList = new ArrayList<>();
        // 初始化界面组件
        initView();
    }

    /**
     * 初始化界面组件和事件监听
     */
    private void initView() {
        rv_list = findViewById(R.id.rv_list);
        et_send_msg = findViewById(R.id.et_send_msg);
        btn_send = findViewById(R.id.btn_send);

        // 设置RecyclerView的布局管理器为线性布局
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        // 创建自定义适配器并绑定数据
        adapter = new ChatAdapter(this);
        adapter.setData(chatBeanList);
        rv_list.setAdapter(adapter);

        // 点击发送按钮事件监听
        btn_send.setOnClickListener(arg0 -> sendData());

        // 在输入框中按下回车键事件监听
        et_send_msg.setOnKeyListener((view, keyCode, keyEvent) -> {
            // 判断是否是回车键并且是按下动作
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                sendData();
                return true;
            }
            return false;
        });

        // 显示欢迎信息和功能说明
        showData(WELCOME_MESSAGE, ChatBean.RECEIVE);
    }

    /**
     * 发送用户输入的数据
     * 从输入框获取文本，清空输入框并显示在聊天列表中，然后根据输入进行处理
     */
    private void sendData() {
        sendMsg = et_send_msg.getText().toString().trim();
        if (TextUtils.isEmpty(sendMsg)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_LONG).show();
            return;
        }
        et_send_msg.setText("");
        // 将用户发送的信息添加到聊天列表中
        showData(sendMsg, ChatBean.SEND);
        // 根据用户输入进行逻辑处理
        processRequest(sendMsg);
    }

    /**
     * 显示聊天数据到列表中
     *
     * @param message 要显示的消息内容
     * @param type    消息的类型（发送或接收）
     */
    private void showData(String message, int type) {
        // 如果当前线程不是主线程，将操作切回主线程执行
        if (Thread.currentThread() != getMainLooper().getThread()) {
            runOnUiThread(() -> showData(message, type));
            return;
        }
        // 创建一个ChatBean并设置消息内容和类型
        ChatBean chatBean = new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(type);
        chatBeanList.add(chatBean);
        // 通知适配器数据已更新
        adapter.notifyDataSetChanged();
        // 滚动到最新消息位置
        rv_list.smoothScrollToPosition(chatBeanList.size() - 1);
    }

    /**
     * 根据用户输入的内容决定调用哪种查询方式：天气查询、天气预报、IP定位或展示功能列表
     *
     * @param input 用户输入的字符串
     */
    private void processRequest(String input) {
        // 判断输入是否以“天气”开头，如"天气北京"
        if (input.startsWith("天气")) {
            String city = input.replace("天气", "").trim();
            if (!TextUtils.isEmpty(city)) {
                if (!city.equals("查询")) {
                    // 查询当前天气
                    queryWeather(city);
                } else {
                    showData("请输入城市名称，例如：'天气北京'", ChatBean.RECEIVE);
                }
            } else {
                showData("请输入城市名称，例如：'天气北京'", ChatBean.RECEIVE);
            }
        }
        // 判断输入是否以“预报天气”开头，如"预报天气北京"
        else if (input.startsWith("预报天气")) {
            String city = input.replace("预报天气", "").trim();
            if (!TextUtils.isEmpty(city)) {
                // 查询天气预报
                queryWeatherForecast(city);
            } else {
                showData("请输入城市名称，例如：'预报天气北京'", ChatBean.RECEIVE);
            }
        }
        // 判断输入是否以“IP定位”开头
        else if (input.startsWith("IP定位")) {
            // 查询IP定位信息
            queryIP();
        }
        // 判断输入是否以“功能”开头，显示功能列表
        else if (input.startsWith("功能")) {
            showData(WELCOME_MESSAGE, ChatBean.RECEIVE);
        }
        // 无法识别的输入，显示默认回复
        else {
            showData(DEFAULT_REPLY, ChatBean.RECEIVE);
        }
    }

    /**
     * 查询城市的实时天气信息，通过城市名称进行地理编码获取adcode，然后再查询天气
     *
     * @param city 用户输入的城市名称
     */
    private void queryWeather(String city) {
        OkHttpClient okHttpClient = new OkHttpClient();

        // 先通过高德地理编码API获取城市的adcode
        Request geoRequest = new Request.Builder()
                .url(AMAP_GEOCODE_URL + "?key=" + AMAP_API_KEY + "&address=" + city)
                .build();

        // 异步请求地理编码信息
        okHttpClient.newCall(geoRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                // 将返回的JSON数据解析为GeocodeResponse对象
                GeocodeResponse geoResponse = gson.fromJson(res, GeocodeResponse.class);

                // 检查是否成功获取到地理编码数据
                if (geoResponse.getGeocodes() != null && !geoResponse.getGeocodes().isEmpty()) {
                    String adcode = geoResponse.getGeocodes().get(0).getAdcode();
                    // 使用adcode查询天气信息
                    getWeatherInfo(adcode, city);
                } else {
                    runOnUiThread(() -> showData("未找到城市 " + city + "，请检查输入是否正确。", ChatBean.RECEIVE));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 查询失败（网络问题等）
                runOnUiThread(() -> showData("网络错误，无法查询城市 " + city, ChatBean.RECEIVE));
            }
        });
    }

    /**
     * 查询城市的天气预报信息，通过城市名称进行地理编码获取adcode，然后再查询天气预报
     *
     * @param city 用户输入的城市名称
     */
    private void queryWeatherForecast(String city) {
        OkHttpClient okHttpClient = new OkHttpClient();

        // 同样先通过地理编码API获取adcode
        Request geoRequest = new Request.Builder()
                .url(AMAP_GEOCODE_URL + "?key=" + AMAP_API_KEY + "&address=" + city)
                .build();

        // 异步请求地理编码信息
        okHttpClient.newCall(geoRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                GeocodeResponse geoResponse = gson.fromJson(res, GeocodeResponse.class);

                // 判断是否成功获取到adcode
                if (geoResponse.getGeocodes() != null && !geoResponse.getGeocodes().isEmpty()) {
                    String adcode = geoResponse.getGeocodes().get(0).getAdcode();
                    // 使用adcode查询天气预报信息
                    getWeatherForecast(adcode, city);
                } else {
                    runOnUiThread(() -> showData("未找到城市 " + city + "，请检查输入是否正确。", ChatBean.RECEIVE));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 查询失败（网络问题等）
                runOnUiThread(() -> showData("网络错误，无法查询城市 " + city, ChatBean.RECEIVE));
            }
        });
    }

    /**
     * 查询用户当前IP对应的位置信息（省份、城市、行政区编码）
     * 使用高德地图IP定位API
     */
    private void queryIP() {
        OkHttpClient okHttpClient = new OkHttpClient();

        String url = AMAP_IP_URL + "?key=" + AMAP_API_KEY;

        Request ipRequest = new Request.Builder()
                .url(url)
                .build();

        // 异步请求IP定位信息
        okHttpClient.newCall(ipRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                IPResponse ipResponse = gson.fromJson(res, IPResponse.class);

                // 检查请求状态
                if ("1".equals(ipResponse.getStatus())) {
                    // 格式化rectangle字段信息
                    String rectangle = ipResponse.getRectangle();
                    String formattedRectangle = formatRectangle(rectangle);

                    String ipInfo = String.format(
                            "IP 查询结果：\n省份：%s\n城市：%s\n行政区编码：%s\n所在区域范围(矩形)：\n%s",
                            ipResponse.getProvince(),
                            ipResponse.getCity(),
                            ipResponse.getAdcode(),
                            formattedRectangle
                    );
                    runOnUiThread(() -> showData(ipInfo, ChatBean.RECEIVE));
                } else {
                    runOnUiThread(() -> showData("IP查询失败：" + ipResponse.getInfo(), ChatBean.RECEIVE));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 网络错误，查询IP失败
                runOnUiThread(() -> showData("网络错误，无法查询 IP 信息。", ChatBean.RECEIVE));
            }
        });
    }

    /**
     * 格式化IP定位返回的rectangle字段（地理范围信息）
     *
     * @param rectangle 原始的rectangle字符串
     * @return 格式化后的字符串
     */
    private String formatRectangle(String rectangle) {
        if (TextUtils.isEmpty(rectangle)) {
            return "未提供矩形范围信息";
        }

        String[] points = rectangle.split(";");
        if (points.length != 2) {
            return "矩形范围格式错误";
        }

        String[] lowerLeft = points[0].split(",");
        String[] upperRight = points[1].split(",");

        if (lowerLeft.length != 2 || upperRight.length != 2) {
            return "矩形范围格式错误";
        }

        return String.format(
                "左下角：经度 %s, 纬度 %s\n右上角：经度 %s, 纬度 %s",
                lowerLeft[0], lowerLeft[1], upperRight[0], upperRight[1]
        );
    }

    /**
     * 使用adcode获取对应城市的实时天气信息
     *
     * @param adcode 城市的adcode（行政区代码）
     * @param city   原始查询的城市名称
     */
    private void getWeatherInfo(String adcode, String city) {
        OkHttpClient okHttpClient = new OkHttpClient();

        // base类型查询实时天气
        Request weatherRequest = new Request.Builder()
                .url(AMAP_WEATHER_URL + "?key=" + AMAP_API_KEY + "&city=" + adcode + "&extensions=base")
                .build();

        // 异步请求天气信息
        okHttpClient.newCall(weatherRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                WeatherResponse weatherResponse = gson.fromJson(res, WeatherResponse.class);

                // 检查是否有天气数据返回
                if (weatherResponse.lives != null && !weatherResponse.lives.isEmpty()) {
                    WeatherResponse.WeatherLive live = weatherResponse.lives.get(0);
                    // 格式化天气信息
                    String weatherInfo = String.format(
                            "城市：%s\n天气：%s\n温度：%s°C\n风向：%s\n风力：%s级\n湿度：%s%%",
                            live.city, live.weather, live.temperature, live.winddirection, live.windpower, live.humidity
                    );
                    runOnUiThread(() -> showData(weatherInfo, ChatBean.RECEIVE));
                } else {
                    runOnUiThread(() -> showData("未能获取城市 " + city + " 的天气信息，请稍后重试。", ChatBean.RECEIVE));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showData("网络错误，无法查询城市 " + city + " 的天气信息。", ChatBean.RECEIVE));
            }
        });
    }

    /**
     * 使用adcode获取对应城市的天气预报信息（未来数日的预报）
     *
     * @param adcode 城市的adcode（行政区代码）
     * @param city   原始查询的城市名称
     */
    private void getWeatherForecast(String adcode, String city) {
        OkHttpClient okHttpClient = new OkHttpClient();

        // all类型查询天气预报
        Request forecastRequest = new Request.Builder()
                .url(AMAP_WEATHER_URL + "?key=" + AMAP_API_KEY + "&city=" + adcode + "&extensions=all")
                .build();

        // 异步请求天气预报信息
        okHttpClient.newCall(forecastRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                WeatherForecastResponse forecastResponse = gson.fromJson(res, WeatherForecastResponse.class);

                // 检查是否有预报数据返回
                if (forecastResponse.forecasts != null && !forecastResponse.forecasts.isEmpty()) {
                    StringBuilder forecastInfo = new StringBuilder("未来几天天气预报：\n");
                    for (WeatherForecastResponse.Forecast forecast : forecastResponse.forecasts.get(0).casts) {
                        forecastInfo.append(String.format(
                                "日期：%s\n白天天气：%s\n夜间天气：%s\n温度：%s°C ~ %s°C\n风力：白天%s级，夜晚%s级\n\n",
                                forecast.date, forecast.dayweather, forecast.nightweather,
                                forecast.nighttemp, forecast.daytemp, forecast.daypower, forecast.nightpower
                        ));
                    }
                    runOnUiThread(() -> showData(forecastInfo.toString(), ChatBean.RECEIVE));
                } else {
                    runOnUiThread(() -> showData("未能获取城市 " + city + " 的天气预报信息，请稍后重试。", ChatBean.RECEIVE));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showData("网络错误，无法查询城市 " + city + " 的天气预报信息。", ChatBean.RECEIVE));
            }
        });
    }
}
