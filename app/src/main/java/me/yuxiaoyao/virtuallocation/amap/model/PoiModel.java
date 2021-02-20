package me.yuxiaoyao.virtuallocation.amap.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

import me.yuxiaoyao.virtuallocation.retrofit.AMapRawJsonDeserializer;

public class PoiModel extends AMapBaseResponse {


    private String count;

    private List<Pois> pois;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public List<Pois> getPois() {
        return pois;
    }

    public void setPois(List<Pois> pois) {
        this.pois = pois;
    }

    public static class Pois {

        private String id;
        private String name;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("typecode")
        private String typeCode;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        private String address;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        private String location;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("pname")
        private String pName;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("citycode")
        private String cityCode;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("cityname")
        private String cityName;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("adcode")
        private String adCode;

        @JsonDeserialize(using = AMapRawJsonDeserializer.class)
        @JsonProperty("adname")
        private String adName;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }


        public String getpName() {
            return pName;
        }

        public void setpName(String pName) {
            this.pName = pName;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getAdCode() {
            return adCode;
        }

        public void setAdCode(String adCode) {
            this.adCode = adCode;
        }

        public String getAdName() {
            return adName;
        }

        public void setAdName(String adName) {
            this.adName = adName;
        }


    }
}
