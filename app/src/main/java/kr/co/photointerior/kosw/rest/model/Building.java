package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * 사용자가 소속된 회사의 건물. 사용자가 활동하는 건물.
 */
public class Building extends ResponseBase {
    @SerializedName("build_seq")
    private String buildingSeq;
    @SerializedName("build_code")
    private String buildingCode;
    @SerializedName("build_name")
    private String buildingName;
    @SerializedName("build_addr")
    private String buildingAddr;
    @SerializedName("place_id")
    private String place_id;
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("build_stair_amt")
    private int build_stair_amt;

    @SerializedName("build_floor_amt")
    private int build_floor_amt;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    @SerializedName("isbuild")
    private String isbuild;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsbuild() {
        return isbuild;
    }

    public void setIsbuild(String isbuild) {
        this.isbuild = isbuild;
    }

    public int getBuild_stair_amt() {
        return build_stair_amt;
    }

    public void setBuild_stair_amt(int build_stair_amt) {
        this.build_stair_amt = build_stair_amt;
    }

    public int getBuild_floor_amt() {
        return build_floor_amt;
    }

    public void setBuild_floor_amt(int build_floor_amt) {
        this.build_floor_amt = build_floor_amt;
    }

    /**
     * 현재 활동지역으로 설정되엇는가 여부. Y-설정됨, N-아님
     */
    @SerializedName("default_flag")
    private String defaultBuilding;
    @SerializedName("comp_name")
    private String companyName;
    @SerializedName("departs")
    private List<Depart> departList;//전체 부서정보
    private Depart depart;//선택 된 부서정보
    /** 회원이 등록한 모든 빌딩의 uuid 전체 */
    @SerializedName("uuids")
    private List<BeaconUuid> beaconUuidList;

    private boolean selected;//뷰상 선택 되었는가 여부

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getBuildingSeq() {
        return buildingSeq;
    }

    public void setBuildingSeq(String buildingSeq) {
        this.buildingSeq = buildingSeq;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getDefaultBuilding() {
        return defaultBuilding;
    }
    public boolean isDefaultBuilding(){
        return "Y".equals(getDefaultBuilding());
    }
    public void setDefaultBuilding(String defaultBuilding) {
        this.defaultBuilding = defaultBuilding;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Depart> getDepartList() {
        return departList;
    }

    public void setDepartList(List<Depart> departList) {
        this.departList = departList;
    }

    public Depart getDepart() {
        return depart;
    }

    public void setDepart(Depart depart) {
        this.depart = depart;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<BeaconUuid> getBeaconUuidList() {
        return beaconUuidList;
    }

    public void setBeaconUuidList(List<BeaconUuid> beaconUuidList) {
        this.beaconUuidList = beaconUuidList;
    }

    public String getBuildingAddr() {
        return buildingAddr;
    }

    public void setBuildingAddr(String buildingAddr) {
        this.buildingAddr = buildingAddr;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String string(){
        return super.string() + "\nBuilding{" +
                "buildingCode='" + buildingCode + '\'' +
                "buildingCode='" + buildingCode + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Building{" +
                "buildingCode='" + buildingCode + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(buildingCode, building.buildingCode) &&
                Objects.equals(buildingName, building.buildingName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(buildingCode, buildingName);
    }
}
