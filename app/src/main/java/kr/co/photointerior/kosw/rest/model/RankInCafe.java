package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class RankInCafe extends ResponseBase {
    @SerializedName("act_amt")
    private String act_amt;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("rank")
    private String rank;
    @SerializedName("catename")
    private String catename;

    private String ismine;

    public String getAct_amt() {
        return act_amt;
    }

    public void setAct_amt(String act_amt) {
        this.act_amt = act_amt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename;
    }

    public String getIsmine() {
        return ismine;
    }

    public void setIsmine(String ismine) {
        this.ismine = ismine;
    }

    @Override
    public String toString() {
        return "RankInCafe{" +
                "act_amt='" + act_amt + '\'' +
                ", nickname='" + nickname + '\'' +
                ", user_seq='" + user_seq + '\'' +
                ", rank='" + rank + '\'' +
                ", catename='" + catename + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankInCafe that = (RankInCafe) o;
        return Objects.equals(act_amt, that.act_amt) &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(user_seq, that.user_seq) &&
                Objects.equals(rank, that.rank) &&
                Objects.equals(catename, that.catename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(act_amt, nickname, user_seq, rank, catename);
    }
}
