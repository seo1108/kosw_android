package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CafeMember extends ResponseBase {
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("regdate")
    private String regdate;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("catename")
    private String catename;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeMember that = (CafeMember) o;
        return Objects.equals(user_name, that.user_name) &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(regdate, that.regdate) &&
                Objects.equals(user_seq, that.user_seq) &&
                Objects.equals(catename, that.catename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_name, nickname, regdate, user_seq, catename);
    }

    @Override
    public String toString() {
        return "CafeMember{" +
                "user_name='" + user_name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", regdate='" + regdate + '\'' +
                ", user_seq='" + user_seq + '\'' +
                ", catename='" + catename + '\'' +
                '}';
    }
}
