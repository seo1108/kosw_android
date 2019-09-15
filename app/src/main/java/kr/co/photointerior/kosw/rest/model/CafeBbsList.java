package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeBbsList extends ResponseBase {
    @SerializedName("list")
    private List<CafeNotice> list;

    public List<CafeNotice> getList() {
        return list;
    }

    public void setList(List<CafeNotice> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeBbsList that = (CafeBbsList) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return "CafeBbsList{" +
                "list=" + list +
                '}';
    }
}
