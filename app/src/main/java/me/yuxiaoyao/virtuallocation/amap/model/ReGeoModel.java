package me.yuxiaoyao.virtuallocation.amap.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import me.yuxiaoyao.virtuallocation.retrofit.AMapRawJsonDeserializer;

public class ReGeoModel extends AMapBaseResponse {

    @JsonProperty("regeocode")
    private GeGeoCode reGeoCode;


    public GeGeoCode getReGeoCode() {
        return reGeoCode;
    }

    public void setReGeoCode(GeGeoCode reGeoCode) {
        this.reGeoCode = reGeoCode;
    }

    public static class GeGeoCode {

        @JsonProperty("formatted_address")
        private String formattedAddress;

        @JsonProperty("addressComponent")
        private AddressComponent addressComponent;


        public AddressComponent getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponent addressComponent) {
            this.addressComponent = addressComponent;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public static class AddressComponent {
            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("city")
            private String city;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("province")
            private String province;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("adcode")
            private String adCode;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("district")
            private String district;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("towncode")
            private String townCode;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("country")
            private String country;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("township")
            private String township;

            @JsonDeserialize(using = AMapRawJsonDeserializer.class)
            @JsonProperty("citycode")
            private String cityCode;


            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getAdcode() {
                return adCode;
            }

            public void setAdcode(String adcode) {
                this.adCode = adcode;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getTownCode() {
                return townCode;
            }

            public void setTownCode(String townCode) {
                this.townCode = townCode;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getTownship() {
                return township;
            }

            public void setTownship(String township) {
                this.township = township;
            }


            public String getCityCode() {
                return cityCode;
            }

            public void setCityCode(String cityCode) {
                this.cityCode = cityCode;
            }


        }

    }
}
