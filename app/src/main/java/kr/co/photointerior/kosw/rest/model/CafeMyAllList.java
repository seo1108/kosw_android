package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeMyAllList extends ResponseBase {
    @SerializedName("adminList")
    private List<Cafe> adminList;

    @SerializedName("joinList")
    private List<Cafe> joinList;

    public List<Cafe> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<Cafe> adminList) {
        this.adminList = adminList;
    }

    public List<Cafe> getJoinList() {
        return joinList;
    }

    public void setJoinList(List<Cafe> joinList) {
        this.joinList = joinList;
    }

    @Override
    public String toString() {
        return "CafeMyAllList{" +
                "adminList=" + adminList +
                ", joinList=" + joinList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeMyAllList that = (CafeMyAllList) o;
        return Objects.equals(adminList, that.adminList) &&
                Objects.equals(joinList, that.joinList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminList, joinList);
    }
}
