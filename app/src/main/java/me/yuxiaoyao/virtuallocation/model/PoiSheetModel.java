package me.yuxiaoyao.virtuallocation.model;

public class PoiSheetModel {

    private String address;
    private Double longitude;
    private Double latitude;
    private String name;
    private String cityName;
    private String cityCode;
    private String adName;
    private String adCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    @Override
    public String toString() {
        return "PoiSheetModel{" +
                "address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", name='" + name + '\'' +
                ", cityName='" + cityName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", adName='" + adName + '\'' +
                ", adCode='" + adCode + '\'' +
                '}';
    }
}