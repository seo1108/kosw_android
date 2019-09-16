package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeMemberList extends ResponseBase {
    @SerializedName("list")
    private List<CafeMember> list;

    public List<CafeMember> getList() {
        return list;
    }

    public void setList(List<CafeMember> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeMemberList that = (CafeMemberList) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return "CafeMemberList{" +
                "list=" + list +
                '}';
    }
}
