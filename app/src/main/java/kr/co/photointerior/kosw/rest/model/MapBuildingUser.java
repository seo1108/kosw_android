package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 사용자가 소속된 회사의 건물. 사용자가 활동하는 건물.
 */
public class MapBuildingUser extends ResponseBase {

    @SerializedName("map_seq")
    private int map_seq;
    @SerializedName("user_seq")
    private int user_seq;
    @SerializedName("build_seq")
    private int build_seq;
    @SerializedName("cust_seq")
    private int cust_seq;
    @SerializedName("cust_name")
    private String cust_name;
    @SerializedName("dept_seq")
    private int dept_seq;
    @SerializedName("dept_name")
    private String dept_name;
    @SerializedName("default_flag")
    private String default_flag;
    @SerializedName("approval_flag")
    private String approval_flag;
    @SerializedName("cust_remarks")
    private String cust_remarks;

    public int getDept_seq() {
        return dept_seq;
    }

    public void setDept_seq(int dept_seq) {
        this.dept_seq = dept_seq;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public int getMap_seq() {
        return map_seq;
    }

    public void setMap_seq(int map_seq) {
        this.map_seq = map_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public int getBuild_seq() {
        return build_seq;
    }

    public void setBuild_seq(int build_seq) {
        this.build_seq = build_seq;
    }

    public int getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(int cust_seq) {
        this.cust_seq = cust_seq;
    }

    public String getDefault_flag() {
        return default_flag;
    }

    public void setDefault_flag(String default_flag) {
        this.default_flag = default_flag;
    }

    public String getApproval_flag() {
        return approval_flag;
    }

    public void setApproval_flag(String approval_flag) {
        this.approval_flag = approval_flag;
    }

    public String getCust_remarks() {
        return cust_remarks;
    }

    public void setCust_remarks(String cust_remarks) {
        this.cust_remarks = cust_remarks;
    }
}
