package cn.itcast.intelrobot.pojo;

import java.util.List;

public class WeatherForecastResponse {
    public List<Forecasts> forecasts;

    public static class Forecasts {
        public List<Forecast> casts;
    }

    public static class Forecast {
        public String date;
        public String dayweather;
        public String nightweather;
        public String daytemp;
        public String nighttemp;
        public String daypower;
        public String nightpower;
    }
}
