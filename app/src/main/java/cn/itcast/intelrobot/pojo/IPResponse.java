package cn.itcast.intelrobot.pojo;

import java.util.List;

public class IPResponse {
    private String status;
    private String info;
    private String province;
    private Object city; // 修改为 Object 类型
    private String adcode;
    private String rectangle;

    public String getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public String getProvince() {
        return province;
    }

    public Object getCity() {
        return city;
    }

    public String getAdcode() {
        return adcode;
    }

    public String getRectangle() {
        return rectangle;
    }

    // 动态获取 city 的值
    public String getCityAsString() {
        if (city instanceof String) {
            return (String) city;
        } else if (city instanceof List) {
            List<?> cityList = (List<?>) city;
            return cityList.isEmpty() ? "未知城市" : cityList.get(0).toString();
        }
        return "未知城市";
    }
}
