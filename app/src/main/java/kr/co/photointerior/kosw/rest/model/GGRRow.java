package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * GGR History
 */
public class GGRRow extends ResponseBase {
    @SerializedName("act_date")
    private String act_date;
    @SerializedName("act_kind")
    private String act_kind;
    @SerializedName("build_seq")
    private String build_seq;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("cust_seq")
    private String cust_seq;
    @SerializedName("act_amt")
    private String act_amt;
    @SerializedName("act_sec")
    private String act_sec;

    public GGRRow(){}

    public String getAct_kind() {
        return act_kind;
    }

    public void setAct_kind(String act_kind) {
        this.act_kind = act_kind;
    }

    public String getAct_sec() {
        return act_sec;
    }

    public void setAct_sec(String act_sec) {
        this.act_sec = act_sec;
    }

    public String getAct_date() {
        return act_date;
    }

    public void setAct_date(String act_date) {
        this.act_date = act_date;
    }

    public String getBuild_seq() {
        return build_seq;
    }

    public void setBuild_seq(String build_seq) {
        this.build_seq = build_seq;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(String cust_seq) {
        this.cust_seq = cust_seq;
    }

    public String getAct_amt() {
        return act_amt;
    }

    public void setAct_amt(String act_amt) {
        this.act_amt = act_amt;
    }

    @Override
    public String toString() {
        return "GGRRow{" +
                "build_seq='" + build_seq + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }


}
