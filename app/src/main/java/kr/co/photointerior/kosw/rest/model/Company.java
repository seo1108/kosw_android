package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 사용자가 소속된 회사 정보.
 * 부서와 건물정보를 포함한다.
 */
public class Company extends ResponseBase {

    @SerializedName("cust_seq")
    private int companySeq;
    @SerializedName("cust_name")
    private String companyName;
    @SerializedName("cust_code")
    private String cust_code;

    @SerializedName("admin_seq")
    private String admin_seq;

    @SerializedName("buildings")
    private List<Building> building;


    @SerializedName("departs")
    private List<Depart> depart;



    public Company(int companySeq, String companyName){
        this.companySeq = companySeq;
        this.companyName = companyName;
    }

    public String getAdmin_seq() {
        return admin_seq;
    }

    public void setAdmin_seq(String admin_seq) {
        this.admin_seq = admin_seq;
    }

    /**
     * 회원이 설정한 현재 빌딩
     */
    @SerializedName("cur_building")
    private Building currentBuilding;

    public String getCust_code() {
        return cust_code;
    }

    public void setCust_code(String cust_code) {
        this.cust_code = cust_code;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanySeq() {
        return companySeq;
    }

    public void setCompanySeq(int companySeq) {
        this.companySeq = companySeq;
    }

    public List<Building> getBuilding() {
        return building;
    }

    public void setBuilding(List<Building> building) {
        this.building = building;
    }

    public List<Depart> getDepart() {
        return depart;
    }

    public void setDepart(List<Depart> depart) {
        this.depart = depart;
    }

    public Building getCurrentBuilding() {
        return currentBuilding;
    }

    public void setCurrentBuilding(Building currentBuilding) {
        this.currentBuilding = currentBuilding;
    }

    public List<String> getDepartTitleList(){
        List<String> list = new ArrayList<>();
        for(Depart d : depart){
            list.add(d.getDepartName());
        }
        return list;
    }

    public String string() {
        return super.string() + "\nCompany{" +
                ", companyName='" + companyName + '\'' +
                ", companySeq='" + companySeq + '\'' +
                ", building='" + (building != null ? pickup(building) : "null") + '\'' +
                ", depart='" + (depart != null ? pickupd(depart) : "null") + '\'' +
                '}';
    }

    private String pickup(List<Building> building){
        StringBuffer sb = new StringBuffer();
        for( Building b : building){
            sb.append(b.string()).append(",\n");
        }
        return sb.toString();
    }

    private String pickupd(List<Depart> depart){
        StringBuffer sb = new StringBuffer();
        for( Depart b : depart){
            sb.append(b.string()).append(",\n");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {

        return Objects.hash(companySeq, companyName);
    }
}
