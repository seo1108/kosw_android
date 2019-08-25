package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CafeDetail extends ResponseBase {
    @SerializedName("cafe")
    private Cafe cafe;

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeDetail that = (CafeDetail) o;
        return Objects.equals(cafe, that.cafe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cafe);
    }

    @Override
    public String toString() {
        return "CafeDetail{" +
                "cafe=" + cafe +
                '}';
    }
}
