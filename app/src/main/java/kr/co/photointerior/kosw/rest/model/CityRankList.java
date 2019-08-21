package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 명예의 전당  List
 */
public class CityRankList extends ResponseBase {


    @SerializedName("W")
    private List<CityRank> wList;
    @SerializedName("A")
    private List<CityRank> aList;
    @SerializedName("K")
    private List<CityRank> kList;

    public List<CityRank> getwList() {
        return wList;
    }

    public void setwList(List<CityRank> wList) {
        this.wList = wList;
    }

    public List<CityRank> getaList() {
        return aList;
    }

    public void setaList(List<CityRank> aList) {
        this.aList = aList;
    }

    public List<CityRank> getkList() {
        return kList;
    }

    public void setkList(List<CityRank> kList) {
        this.kList = kList;
    }
}
