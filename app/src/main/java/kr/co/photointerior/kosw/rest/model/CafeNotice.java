package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeNotice extends ResponseBase {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("regdate")
    private String regdate;
    @SerializedName("bbsseq")
    private String bbsseq;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("content")
    private String content;
    @SerializedName("comments")
    private List<Comment> comments;

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

    public String getBbsseq() {
        return bbsseq;
    }

    public void setBbsseq(String bbsseq) {
        this.bbsseq = bbsseq;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeNotice that = (CafeNotice) o;
        return Objects.equals(nickname, that.nickname) &&
                Objects.equals(regdate, that.regdate) &&
                Objects.equals(bbsseq, that.bbsseq) &&
                Objects.equals(user_seq, that.user_seq) &&
                Objects.equals(content, that.content) &&
                Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, regdate, bbsseq, user_seq, content, comments);
    }

    @Override
    public String toString() {
        return "CafeNotice{" +
                "nickname='" + nickname + '\'' +
                ", regdate='" + regdate + '\'' +
                ", bbsseq='" + bbsseq + '\'' +
                ", user_seq='" + user_seq + '\'' +
                ", content='" + content + '\'' +
                ", comments=" + comments +
                '}';
    }
}
