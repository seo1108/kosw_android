package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * GGR history List
 */
public class GGRList extends ResponseBase {


    @SerializedName("GOLD")
    private List<GGRRow> goldList;
    @SerializedName("GREEN")
    private List<GGRRow> greenList;
    @SerializedName("RED")
    private List<GGRRow> redList;
    @SerializedName("WALK")
    private List<GGRRow> walkList;

    public List<GGRRow> getGoldList() {
        return goldList;
    }

    public void setGoldList(List<GGRRow> goldList) {
        this.goldList = goldList;
    }

    public List<GGRRow> getGreenList() {
        return greenList;
    }

    public void setGreenList(List<GGRRow> greenList) {
        this.greenList = greenList;
    }

    public List<GGRRow> getRedList() {
        return redList;
    }

    public void setRedList(List<GGRRow> redList) {
        this.redList = redList;
    }

    public List<GGRRow> getWalkList() {
        return walkList;
    }

    public void setWalkList(List<GGRRow> walkList) {
        this.walkList = walkList;
    }
}
