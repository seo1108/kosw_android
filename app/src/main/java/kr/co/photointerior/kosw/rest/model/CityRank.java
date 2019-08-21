package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 명예의 전당
 */
public class CityRank extends ResponseBase {
    @SerializedName("act_date")
    private String act_date;
    @SerializedName("champ_kind")
    private String champ_kind;
    @SerializedName("country")
    private String country;
    @SerializedName("city")
    private String city;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("act_time")
    private String act_time;
    @SerializedName("act_amt")
    private String act_amt;

    public CityRank(){}

    public String getAct_date() {
        return act_date;
    }

    public void setAct_date(String act_date) {
        this.act_date = act_date;
    }

    public String getChamp_kind() {
        return champ_kind;
    }

    public void setChamp_kind(String champ_kind) {
        this.champ_kind = champ_kind;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getAct_time() {
        return act_time;
    }

    public void setAct_time(String act_time) {
        this.act_time = act_time;
    }

    public String getAct_amt() {
        return act_amt;
    }

    public void setAct_amt(String act_amt) {
        this.act_amt = act_amt;
    }

    @Override
    public String toString() {
        return "CityRank{" +
                "act_date='" + act_date + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }


}
