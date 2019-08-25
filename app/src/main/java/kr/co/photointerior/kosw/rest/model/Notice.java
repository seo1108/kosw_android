package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Notice {
    @SerializedName("notiseq")
    private String notiseq;
    @SerializedName("contents")
    private String contents;
    @SerializedName("regdate")
    private String regdate;

    public String getNotiseq() {
        return notiseq;
    }

    public void setNotiseq(String notiseq) {
        this.notiseq = notiseq;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return Objects.equals(notiseq, notice.notiseq) &&
                Objects.equals(contents, notice.contents) &&
                Objects.equals(regdate, notice.regdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notiseq, contents, regdate);
    }

    @Override
    public String toString() {
        return "Notice{" +
                "notiseq='" + notiseq + '\'' +
                ", contents='" + contents + '\'' +
                ", regdate='" + regdate + '\'' +
                '}';
    }
}
