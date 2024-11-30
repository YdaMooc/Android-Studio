package cn.itcast.intelrobot.pojo;

public class CurrentWeather {

    private String province; // 省份
    private String city; // 城市
    private String adcode; // 区域编码
    private String weather; // 天气
    private String temperature; // 温度
    private String winddirection; // 风向
    private String windpower; // 风力
    private String humidity; // 空气湿度
    private String reporttime; // 数据发布时间
    private String temperature_float; // 温度（浮点数）
    private String humidity_float; // 空气湿度（浮点数）

    // Getters and Setters
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindpower() {
        return windpower;
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public String getTemperature_float() {
        return temperature_float;
    }

    public void setTemperature_float(String temperature_float) {
        this.temperature_float = temperature_float;
    }

    public String getHumidity_float() {
        return humidity_float;
    }

    public void setHumidity_float(String humidity_float) {
        this.humidity_float = humidity_float;
    }
}

