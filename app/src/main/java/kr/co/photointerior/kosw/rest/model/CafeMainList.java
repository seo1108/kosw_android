package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * 메인화면 표출용 카페 리스트
 */
public class CafeMainList extends ResponseBase {
    @SerializedName("cafelist")
    private List<Cafe> cafelist;

    public List<Cafe> getCafelist() {
        return cafelist;
    }

    public void setCafelist(List<Cafe> cafelist) {
        this.cafelist = cafelist;
    }

    @Override
    public String toString() {
        return "CafeMainList{" +
                "cafelist=" + cafelist +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeMainList that = (CafeMainList) o;
        return Objects.equals(cafelist, that.cafelist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cafelist);
    }
}
