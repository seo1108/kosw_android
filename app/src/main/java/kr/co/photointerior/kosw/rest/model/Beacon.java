package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자가 소속된 회사의 건물. 사용자가 활동하는 건물.
 */
public class Beacon extends ResponseBase {
    @SerializedName("beacon_seq")
    private int beacon_seq;
    @SerializedName("manufac_seq")
    private int manufac_seq;
    @SerializedName("admin_seq")
    private int admin_seq;
    @SerializedName("beacon_uuid")
    private String beacon_uuid;
    @SerializedName("major_value")
    private int major_value;
    @SerializedName("minor_value")
    private int minor_value;
    @SerializedName("install_floor")
    private int install_floor;
    @SerializedName("cust_seq")
    private int cust_seq;

    public int getBeacon_seq() {
        return beacon_seq;
    }

    public void setBeacon_seq(int beacon_seq) {
        this.beacon_seq = beacon_seq;
    }

    public int getManufac_seq() {
        return manufac_seq;
    }

    public void setManufac_seq(int manufac_seq) {
        this.manufac_seq = manufac_seq;
    }

    public int getAdmin_seq() {
        return admin_seq;
    }

    public void setAdmin_seq(int admin_seq) {
        this.admin_seq = admin_seq;
    }

    public String getBeacon_uuid() {
        return beacon_uuid;
    }

    public void setBeacon_uuid(String beacon_uuid) {
        this.beacon_uuid = beacon_uuid;
    }

    public int getMajor_value() {
        return major_value;
    }

    public void setMajor_value(int major_value) {
        this.major_value = major_value;
    }

    public int getMinor_value() {
        return minor_value;
    }

    public void setMinor_value(int minor_value) {
        this.minor_value = minor_value;
    }

    public int getInstall_floor() {
        return install_floor;
    }

    public void setInstall_floor(int install_floor) {
        this.install_floor = install_floor;
    }

    public int getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(int cust_seq) {
        this.cust_seq = cust_seq;
    }
}
