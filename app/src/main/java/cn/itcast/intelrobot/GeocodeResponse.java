package cn.itcast.intelrobot;

import java.util.List;

public class GeocodeResponse {
    private List<Geocode> geocodes;

    public List<Geocode> getGeocodes() {
        return geocodes;
    }

    public static class Geocode {
        private String adcode;

        public String getAdcode() {
            return adcode;
        }
    }
}
