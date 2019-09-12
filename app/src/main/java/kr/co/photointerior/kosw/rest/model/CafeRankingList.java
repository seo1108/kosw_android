package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeRankingList extends ResponseBase {
    @SerializedName("mine")
    private RankInCafe mine;
    @SerializedName("list")
    private List<RankInCafe> list;

    public RankInCafe getMine() {
        return mine;
    }

    public void setMine(RankInCafe mine) {
        this.mine = mine;
    }

    public List<RankInCafe> getList() {
        return list;
    }

    public void setList(List<RankInCafe> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CafeRankingList{" +
                "mine=" + mine +
                ", list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeRankingList that = (CafeRankingList) o;
        return Objects.equals(mine, that.mine) &&
                Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mine, list);
    }
}
