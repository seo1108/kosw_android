package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class MyInfo extends ResponseBase {
    @SerializedName("additions")
    private String additions;
    @SerializedName("regdate")
    private String regdate;
    @SerializedName("isAdmin")
    private String isAdmin;
    @SerializedName("status")
    private String status;
    @SerializedName("expdate")
    private String expdate;

    public String getAdditions() {
        return additions;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyInfo myInfo = (MyInfo) o;
        return Objects.equals(additions, myInfo.additions) &&
                Objects.equals(regdate, myInfo.regdate) &&
                Objects.equals(isAdmin, myInfo.isAdmin) &&
                Objects.equals(status, myInfo.status) &&
                Objects.equals(expdate, myInfo.expdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(additions, regdate, isAdmin, status, expdate);
    }

    @Override
    public String toString() {
        return "MyInfo{" +
                "additions='" + additions + '\'' +
                ", regdate='" + regdate + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", status='" + status + '\'' +
                ", expdate='" + expdate + '\'' +
                '}';
    }
}
