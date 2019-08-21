package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자가 소속된 회사 정보.
 * 부서와 건물정보를 포함한다.
 */
public class Buildings extends ResponseBase {

    @SerializedName("buildings")
    private List<Building> buildings;

    public List<Building> getBuilding() {
        return buildings;
    }
    public void setBuilding(List<Building> building) {
        this.buildings = building;
    }

    public String string(){
        return  "1111111" ;
    }
    public  String toString() { return  "2222222" ;}
}
