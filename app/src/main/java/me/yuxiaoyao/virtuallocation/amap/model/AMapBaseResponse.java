package me.yuxiaoyao.virtuallocation.amap.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AMapBaseResponse {
    @JsonProperty("infocode")
    private String infoCode;
    private String status;
    private String info;

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
