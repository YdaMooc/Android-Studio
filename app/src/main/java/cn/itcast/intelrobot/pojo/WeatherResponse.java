package cn.itcast.intelrobot.pojo;

import java.util.List;

public class WeatherResponse {

    private String city; // 城市名
    private String adcode; // 城市代码
    private String province; // 省份
    private String reporttime; // 数据发布时间
    private List<Cast> casts; // 未来几天的天气预报

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public List<Cast> getCasts() {
        return casts;
    }

    public void setCasts(List<Cast> casts) {
        this.casts = casts;
    }

    public static class Cast {
        private String date; // 日期
        private String week; // 星期
        private String dayweather; // 白天天气
        private String nightweather; // 晚上天气
        private String daytemp; // 白天温度
        private String nighttemp; // 晚上温度
        private String daywind; // 白天风向
        private String nightwind; // 晚上风向
        private String daypower; // 白天风力
        private String nightpower; // 晚上风力
        private String daytemp_float; // 白天温度(浮点数)
        private String nighttemp_float; // 晚上温度(浮点数)

        // Getters and Setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getDayweather() {
            return dayweather;
        }

        public void setDayweather(String dayweather) {
            this.dayweather = dayweather;
        }

        public String getNightweather() {
            return nightweather;
        }

        public void setNightweather(String nightweather) {
            this.nightweather = nightweather;
        }

        public String getDaytemp() {
            return daytemp;
        }

        public void setDaytemp(String daytemp) {
            this.daytemp = daytemp;
        }

        public String getNighttemp() {
            return nighttemp;
        }

        public void setNighttemp(String nighttemp) {
            this.nighttemp = nighttemp;
        }

        public String getDaywind() {
            return daywind;
        }

        public void setDaywind(String daywind) {
            this.daywind = daywind;
        }

        public String getNightwind() {
            return nightwind;
        }

        public void setNightwind(String nightwind) {
            this.nightwind = nightwind;
        }

        public String getDaypower() {
            return daypower;
        }

        public void setDaypower(String daypower) {
            this.daypower = daypower;
        }

        public String getNightpower() {
            return nightpower;
        }

        public void setNightpower(String nightpower) {
            this.nightpower = nightpower;
        }

        public String getDaytemp_float() {
            return daytemp_float;
        }

        public void setDaytemp_float(String daytemp_float) {
            this.daytemp_float = daytemp_float;
        }

        public String getNighttemp_float() {
            return nighttemp_float;
        }

        public void setNighttemp_float(String nighttemp_float) {
            this.nighttemp_float = nighttemp_float;
        }
    }
}

