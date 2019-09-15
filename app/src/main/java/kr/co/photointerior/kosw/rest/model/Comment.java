package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Comment extends ResponseBase {
    @SerializedName("commentseq")
    private String commentseq;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("regdate")
    private String regdate;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("content")
    private String content;
    @SerializedName("charImageFile")
    private String charImageFile;

    public String getCommentseq() {
        return commentseq;
    }

    public void setCommentseq(String commentseq) {
        this.commentseq = commentseq;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCharImageFile() {
        return charImageFile;
    }

    public void setCharImageFile(String charImageFile) {
        this.charImageFile = charImageFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(commentseq, comment.commentseq) &&
                Objects.equals(nickname, comment.nickname) &&
                Objects.equals(regdate, comment.regdate) &&
                Objects.equals(user_seq, comment.user_seq) &&
                Objects.equals(content, comment.content) &&
                Objects.equals(charImageFile, comment.charImageFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentseq, nickname, regdate, user_seq, content, charImageFile);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentseq='" + commentseq + '\'' +
                ", nickname='" + nickname + '\'' +
                ", regdate='" + regdate + '\'' +
                ", user_seq='" + user_seq + '\'' +
                ", content='" + content + '\'' +
                ", charImageFile='" + charImageFile + '\'' +
                '}';
    }
}
