package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 사용자가 소속된 회사 정보.
 * 부서와 건물정보를 포함한다.
 */
public class Companys extends ResponseBase {

    public List<Company> getCompanys() {
        return companys;
    }

    public void setCompanys(List<Company> companys) {
        this.companys = companys;
    }

    @SerializedName("companys")
    private List<Company> companys;


}
