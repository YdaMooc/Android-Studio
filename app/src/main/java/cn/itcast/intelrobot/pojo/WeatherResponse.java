package cn.itcast.intelrobot.pojo;

import java.util.List;

public class WeatherResponse {
    public List<WeatherLive> lives;

    public static class WeatherLive {
        public String city;
        public String weather;
        public String temperature;
        public String winddirection;
        public String windpower;
        public String humidity;
    }
}
