package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 사용자가 소속된 회사 정보.
 * 부서와 건물정보를 포함한다.
 */
public class Departs extends ResponseBase {


    @SerializedName("departs")
    private List<Depart> departs;

    public List<Depart> getDeparts() {
        return departs;
    }

    public void setDeparts(List<Depart> departs) {
        this.departs = departs;
    }

}
