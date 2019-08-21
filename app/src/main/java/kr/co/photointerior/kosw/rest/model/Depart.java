package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * 사용자의 부서정보
 */
public class Depart extends ResponseBase {
    @SerializedName("dept_seq")
    private int departSeq;
    @SerializedName("dept_name")
    private String departName;
    @SerializedName("admin_seq")
    private String adminSeq;

    public Depart(){}
    public Depart(int departSeq, String departName){
        this.departSeq = departSeq;
        this.departName = departName;
    }
    public int getDepartSeq() {
        return departSeq;
    }

    public void setDepartSeq(int departSeq) {
        this.departSeq = departSeq;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getAdminSeq() {
        return adminSeq;
    }

    public void setAdminSeq(String adminSeq) {
        this.adminSeq = adminSeq;
    }

    public String string(){
        return super.string() + "\nDepart{" +
                "departSeq='" + departSeq + '\'' +
                ", departName='" + departName + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Depart{" +
                "departSeq='" + departSeq + '\'' +
                ", departName='" + departName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Depart depart = (Depart) o;
        return Objects.equals(departSeq, depart.departSeq) &&
                Objects.equals(departName, depart.departName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(departSeq, departName);
    }
}
