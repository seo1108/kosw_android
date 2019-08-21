package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * 사용자가 소속된 회사의 건물. 사용자가 활동하는 건물.
 */
public class BuildingStair extends ResponseBase {
    @SerializedName("stair_seq")
    private int stair_seq;
    @SerializedName("build_seq")
    private int build_seq;
    @SerializedName("admin_seq")
    private int admin_seq;
    @SerializedName("stair_name")
    private String stair_name;
    @SerializedName("cust_seq")
    private int cust_seq;


    public int getStair_seq() {
        return stair_seq;
    }

    public void setStair_seq(int stair_seq) {
        this.stair_seq = stair_seq;
    }

    public int getBuild_seq() {
        return build_seq;
    }

    public void setBuild_seq(int build_seq) {
        this.build_seq = build_seq;
    }

    public int getAdmin_seq() {
        return admin_seq;
    }

    public void setAdmin_seq(int admin_seq) {
        this.admin_seq = admin_seq;
    }

    public String getStair_name() {
        return stair_name;
    }

    public void setStair_name(String stair_name) {
        this.stair_name = stair_name;
    }

    public int getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(int cust_seq) {
        this.cust_seq = cust_seq;
    }
}
