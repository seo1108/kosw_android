package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * 사용자가 소속된 회사의 건물. 사용자가 활동하는 건물.
 */
public class Admin extends ResponseBase {
    @SerializedName("admin_seq")
    private String admin_seq;
    @SerializedName("cust_seq")
    private String cust_seq;
    @SerializedName("email")
    private String email;
    @SerializedName("passwd")
    private String passwd;
    @SerializedName("admin_name")
    private String admin_name;
    @SerializedName("admin_phone")
    private String admin_phone;
    @SerializedName("active_flag")
    private String active_flag;
    @SerializedName("admin_reg_time")
    private String admin_reg_time;

    @SerializedName("post_phone")
    private String post_phone;

    @SerializedName("cust_name")
    private String cust_name;

    @SerializedName("approval_flag")
    private String approval_flag;

    public String getApproval_flag() {
        return approval_flag;
    }

    public void setApproval_flag(String approval_flag) {
        this.approval_flag = approval_flag;
    }

    public String getPost_phone() {
        return post_phone;
    }

    public void setPost_phone(String post_phone) {
        this.post_phone = post_phone;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    @SerializedName("cust_name")

    public String getAdmin_seq() {
        return admin_seq;
    }

    public void setAdmin_seq(String admin_seq) {
        this.admin_seq = admin_seq;
    }

    public String getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(String cust_seq) {
        this.cust_seq = cust_seq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public String getAdmin_phone() {
        return admin_phone;
    }

    public void setAdmin_phone(String admin_phone) {
        this.admin_phone = admin_phone;
    }

    public String getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(String active_flag) {
        this.active_flag = active_flag;
    }

    public String getAdmin_reg_time() {
        return admin_reg_time;
    }

    public void setAdmin_reg_time(String admin_reg_time) {
        this.admin_reg_time = admin_reg_time;
    }
}
