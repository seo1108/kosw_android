package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 소속 회사의 로고와 색상
 */
public class Logo extends ResponseBase {
    @SerializedName("logo")
    private String logoUrl;
    @SerializedName("color")
    private String hexColor;
    @SerializedName("code")
    private String buildingCode;

    @SerializedName("addr")
    private String buildingAddr;
    public String getBuildingAddr() {
        return buildingAddr;
    }

    public void setBuildingAddr(String buildingAddr) {
        this.buildingAddr = buildingAddr;
    }


    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String string() {
        return super.string() + ",\nLogo{" +
                "logoUrl='" + logoUrl + '\'' +
                ", hexColor='" + hexColor + '\'' +
                ", buildingCode='" + buildingCode + '\'' +
                '}';
    }
}
