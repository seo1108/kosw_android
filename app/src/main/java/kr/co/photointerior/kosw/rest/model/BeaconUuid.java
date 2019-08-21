package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 비콘 UUID 클래스.
 */
public class BeaconUuid extends ResponseBase implements Serializable {
    private int localSeq;
    @SerializedName("beacon_uuid")
    private String uuid;
    @SerializedName("major_value")
    private String majorValue;
    @SerializedName("minor_value")
    private String minorValue;
    @SerializedName("install_floor")
    private String installFloor;
    @SerializedName("beacon_seq")
    private String beaconSeq;
    /** 비콘 원래 관리번호*/
    private String originalBeaconSeq;
    @SerializedName("stair_seq")
    private String stairSeq;
    @SerializedName("build_seq")
    private String buildSeq;
    @SerializedName("cust_seq")
    private String custSeq;
    @SerializedName("build_code")
    private String buildCode;
    @SerializedName("godo")
    private String altitude;

    @SerializedName("toady_amount")
    private String todayGoUpAmount;
    @SerializedName("toady_ranking")
    private String todayRanking;
    @SerializedName("company_logo")
    private Logo companyLogo;
    /** 현재 설정된 건물의 계단 수 */
    @SerializedName("floor_amount")
    private String floorAmount;
    /** 현재 설정된 건물의 계단 수 */
    @SerializedName("stair_amount")
    private String stairAmount;
    @SerializedName("mainCharImageFile")
    private String mainCharFilename;
    @SerializedName("subCharImageFile")
    private String subCharFilenane;
    @SerializedName("uuids")
    private List<BeaconUuid> beaconUuidList;
    /** 비콘을 인식하고 올라간 층수를 계산한 값 */
    private int goUpAmount;

    private long floorSettingTime;

    public int getLocalSeq() {
        return localSeq;
    }

    public void setLocalSeq(int localSeq) {
        this.localSeq = localSeq;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajorValue() {
        return majorValue;
    }

    public void setMajorValue(String majorValue) {
        this.majorValue = majorValue;
    }

    public String getMinorValue() {
        return minorValue;
    }
    public int getMinorValueToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getMinorValue()) ? 0 : Float.valueOf(getMinorValue()).intValue();
    }
    public void setMinorValue(String minorValue) {
        this.minorValue = minorValue;
    }

    public String getInstallFloor() {
        return installFloor;
    }
    public int getInstallFloorToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getInstallFloor()) ? 0 : Integer.valueOf(getInstallFloor());
    }
    public void setInstallFloor(String installFloor) {
        this.installFloor = installFloor;
    }

    public String getBeaconSeq() {
        return beaconSeq;
    }
    public int getBeaconSeqToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getBeaconSeq()) ? 0 : Integer.valueOf(getBeaconSeq());
    }

    public void setBeaconSeq(String beaconSeq) {
        this.beaconSeq = beaconSeq;
    }

    public String getOriginalBeaconSeq() {
        return originalBeaconSeq;
    }

    public void setOriginalBeaconSeq(String originalBeaconSeq) {
        this.originalBeaconSeq = originalBeaconSeq;
    }

    public String getStairSeq() {
        return stairSeq;
    }

    public void setStairSeq(String stairSeq) {
        this.stairSeq = stairSeq;
    }

    public String getBuildSeq() {
        return buildSeq;
    }

    public void setBuildSeq(String buildSeq) {
        this.buildSeq = buildSeq;
    }

    public String getCustSeq() {
        return custSeq;
    }

    public void setCustSeq(String custSeq) {
        this.custSeq = custSeq;
    }

    public String getBuildCode() {
        return buildCode;
    }

    public void setBuildCode(String buildCode) {
        this.buildCode = buildCode;
    }

    /**
     * 서어베서 설정한 층의 고도
     * @return
     */
    public String getAltitude() {
        return altitude;
    }
    public double getAltitudeToDouble(){
        return StringUtil.isEmptyOrWhiteSpace(getAltitude()) ? 0.0 : Double.valueOf(getAltitude());
    }
    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    /**
     * @return 지금 올라간 층수. 비콘인식으로 계산된 값.
     */
    public int getGoUpAmount() {
        return goUpAmount;
    }

    public void setGoUpAmount(int goUpAmount) {
        this.goUpAmount = goUpAmount;
    }

    /**
     * @return 오늘 올라간 층수 : 서버에서 오는 값.
     */
    public String getTodayGoUpAmount() {
        return todayGoUpAmount;
    }
    public int getTodayGoUpAmountToInt(){
        return Integer.valueOf( StringUtil.isEmptyOrWhiteSpace(getTodayGoUpAmount()) ? "0" : getTodayGoUpAmount());
    }
    public void setTodayGoUpAmount(String todayGoUpAmount) {
        this.todayGoUpAmount = todayGoUpAmount;
    }

    /**
     * 특정 계단에서 활동한 랭킹 : 서버에서 오는 값.
     * @return
     */
    public String getTodayRanking() {
        return todayRanking;
    }

    public void setTodayRanking(String todayRanking) {
        this.todayRanking = todayRanking;
    }

    public Logo getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(Logo companyLogo) {
        this.companyLogo = companyLogo;
    }

    /**
     * @return 현재 설정된 건물의 계단 수
     */
    public String getFloorAmount() {
        return floorAmount;
    }
    public int getFloorAmountToInt(){
       return Integer.valueOf( StringUtil.isEmptyOrWhiteSpace(getFloorAmount()) ? "0" : getFloorAmount());
    }
    public void setFloorAmount(String floorAmount) {
        this.floorAmount = floorAmount;
    }

    /**
     * @return 건물 1개층의 개단 수
     */
    public String getStairAmount() {
        return stairAmount;
    }
    public int getStairAmountToInt(){
        return Integer.valueOf( StringUtil.isEmptyOrWhiteSpace(getStairAmount()) ? "0" : getStairAmount());
    }
    public void setStairAmount(String stairAmount) {
        this.stairAmount = stairAmount;
    }
    public String getMainCharFilename() {
        return mainCharFilename;
    }

    public void setMainCharFilename(String mainCharFilename) {
        this.mainCharFilename = mainCharFilename;
    }

    public String getSubCharFilenane() {
        return subCharFilenane;
    }

    public void setSubCharFilenane(String subCharFilenane) {
        this.subCharFilenane = subCharFilenane;
    }

    public List<BeaconUuid> getBeaconUuidList() {
        return beaconUuidList;
    }

    public void setBeaconUuidList(List<BeaconUuid> beaconUuidList) {
        this.beaconUuidList = beaconUuidList;
    }

    public long getFloorSettingTime() {
        return floorSettingTime;
    }

    public void setFloorSettingTime(long floorSettingTime) {
        this.floorSettingTime = floorSettingTime;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer(super.string() + ",\nBeaconUuid{");
        sb.append("localSeq='").append(localSeq).append('\'');
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", majorValue='").append(majorValue).append('\'');
        sb.append(", minorValue='").append(minorValue).append('\'');
        sb.append(", installFloor='").append(installFloor).append('\'');
        sb.append(", beaconSeq='").append(beaconSeq).append('\'');
        sb.append(", originalBeaconSeq='").append(originalBeaconSeq).append('\'');
        sb.append(", stairSeq='").append(stairSeq).append('\'');
        sb.append(", buildSeq='").append(buildSeq).append('\'');
        sb.append(", custSeq='").append(custSeq).append('\'');
        sb.append(", buildCode='").append(buildCode).append('\'');
        sb.append(", altitude='").append(altitude).append('\'');
        sb.append(", goUpAmount='").append(goUpAmount).append('\'');
        sb.append(", todayGoUpAmount='").append(todayGoUpAmount).append('\'');
        sb.append(", todayRanking='").append(todayRanking).append('\'');
        sb.append(", floorAmount='").append(floorAmount).append('\'');
        sb.append(", stairAmount='").append(stairAmount).append('\'');
        sb.append(", mainCharFilename='").append(mainCharFilename).append('\'');
        sb.append(", subCharFilenane='").append(subCharFilenane).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /** 서버로 전송할 데이터를 보유한 Map 생성 반환 */
    public Map<String, Object> getQueryMap(){
        Map<String, Object> map = KUtil.getDefaultQueryMap();
        map.put("beacon_uuid", getUuid());
        map.put("major_value", getMajorValue());
        map.put("minor_value", getMinorValue());
        map.put("install_floor", getInstallFloor());
        //map.put("beacon_seq", getBeaconSeq());
        map.put("beacon_seq", getOriginalBeaconSeq());
        map.put("stair_seq", getStairSeq());
        map.put("build_seq", getBuildSeq());
        map.put("floor_amount", getFloorAmount());
        map.put("stair_amount", getStairAmount());
        map.put("cust_seq", getCustSeq());
        map.put("build_code", getBuildCode());
        map.put("godo", getAltitude());
        map.put("goup_amt", getGoUpAmount());
        return map;
    }

    public BeaconUuid deepCopy(){
        BeaconUuid bu = new BeaconUuid();
        bu.setUuid(getUuid());
        bu.setMajorValue(getMajorValue());
        bu.setMinorValue(getMinorValue());
        bu.setInstallFloor(getInstallFloor());
        bu.setBeaconSeq(getBeaconSeq());
        bu.setOriginalBeaconSeq(getOriginalBeaconSeq());
        bu.setStairSeq(getStairSeq());
        bu.setBuildSeq(getBuildSeq());
        bu.setFloorAmount(getFloorAmount());
        bu.setStairAmount(getStairAmount());
        bu.setCustSeq(getCustSeq());
        bu.setBuildCode(getBuildCode());
        bu.setAltitude(getAltitude());
        bu.setGoUpAmount(getGoUpAmount());
        return bu;
    }
}
