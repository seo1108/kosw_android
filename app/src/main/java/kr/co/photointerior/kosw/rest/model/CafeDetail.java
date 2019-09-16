package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CafeDetail extends ResponseBase {
    @SerializedName("cafe")
    private Cafe cafe;
    @SerializedName("myInfo")
    private MyInfo myInfo;

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public MyInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MyInfo myInfo) {
        this.myInfo = myInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeDetail that = (CafeDetail) o;
        return Objects.equals(cafe, that.cafe) &&
                Objects.equals(myInfo, that.myInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cafe, myInfo);
    }

    @Override
    public String toString() {
        return "CafeDetail{" +
                "cafe=" + cafe +
                ", myInfo=" + myInfo +
                '}';
    }
}
